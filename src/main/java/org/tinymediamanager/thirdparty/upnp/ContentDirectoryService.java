package org.tinymediamanager.thirdparty.upnp;

import java.beans.PropertyChangeSupport;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.contentdirectory.DIDLParser;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.StorageFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.movie.MovieList;
import org.tinymediamanager.core.tvshow.TvShowList;
import org.tinymediamanager.core.tvshow.entities.TvShowEpisode;
import org.tinymediamanager.scraper.DynaComparator;
import org.tinymediamanager.ui.UTF8Control;

public class ContentDirectoryService extends AbstractContentDirectoryService {

  public ContentDirectoryService() {
    super();
  }

  public ContentDirectoryService(List<String> searchCapabilities, List<String> sortCapabilities, PropertyChangeSupport propertyChangeSupport) {
    super(searchCapabilities, sortCapabilities, propertyChangeSupport);
  }

  public ContentDirectoryService(List<String> searchCapabilities, List<String> sortCapabilities) {
    super(searchCapabilities, sortCapabilities);
  }

  private static final Logger         LOGGER = LoggerFactory.getLogger(ContentDirectoryService.class);
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  @Override
  public BrowseResult browse(String objectID, BrowseFlag browseFlag, String filter, long firstResult, long maxResults, SortCriterion[] orderby)
      throws ContentDirectoryException {
    try {
      LOGGER.debug("ObjectId: " + objectID);
      LOGGER.debug("BrowseFlag: " + browseFlag);
      LOGGER.debug("Filter: " + filter);
      LOGGER.debug("FirstResult: " + firstResult);
      LOGGER.debug("MaxResults: " + maxResults);
      LOGGER.debug("OrderBy: " + SortCriterion.toString(orderby));

      String orderMovie = "getTitle";
      String orderShow = "getTitle";
      // if (SortCriterion.toString(orderby).contains("dc:date")) {
      // orderMovie = "getReleaseDateFormatted";
      // orderShow = "getFirstAired";
      // }

      DIDLContent didl = new DIDLContent();

      String[] path = StringUtils.split(objectID, '/');
      if (path == null) {
        throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, "path was NULL");
      }
      // [0] = 0/1/2
      // [1] = UUID
      // [2] = Season
      // [3] = Episode

      // =====================================================
      // get full metadata of ONE object (after clicking on ITEM)
      // =====================================================
      if (browseFlag.equals(BrowseFlag.METADATA)) {

        // if BrowseFlag is set to "BrowseMetadata", then TotalMatches shall be set to 1 (5.5.8.2)
        StorageFolder cont = new StorageFolder();
        cont.setChildCount(1);

        if (path[0].equals(Upnp.ID_ROOT)) {
          cont.setId(Upnp.ID_ROOT);
          cont.setParentID("-1");
          cont.setTitle("All");
          didl.addContainer(cont);
        }
        else if (path[0].equals(Upnp.ID_MOVIES)) {
          if (path.length == 2) {
            // single movie
            org.tinymediamanager.core.movie.entities.Movie m = MovieList.getInstance().lookupMovie(UUID.fromString(path[1]));
            if (m != null) {
              didl.addItem(Metadata.getUpnpMovie(m, true));
            }
          }
          else {
            // just "movies" container
            cont.setId(Upnp.ID_MOVIES);
            cont.setParentID(Upnp.ID_ROOT);
            cont.setTitle(BUNDLE.getString("tmm.movies"));
            didl.addContainer(cont);
          }
        }
        else if (path[0].equals(Upnp.ID_TVSHOWS)) {
          if (path.length > 1) {
            // single show
            org.tinymediamanager.core.tvshow.entities.TvShow t = TvShowList.getInstance().lookupTvShow(UUID.fromString(path[1]));
            if (t != null) {
              TvShowEpisode ep = t.getEpisode(getInt(path[2]), getInt(path[3]));
              if (ep != null) {
                didl.addItem(Metadata.getUpnpTvShowEpisode(t, ep, true));
              }
            }
          }
          else {
            // just show container
            cont.setId(Upnp.ID_TVSHOWS);
            cont.setParentID(Upnp.ID_ROOT);
            cont.setTitle(BUNDLE.getString("tmm.tvshows"));
            didl.addContainer(cont);
          }
        }
        return returnResult(didl);

      }
      // =====================================================
      // Browse directory (after clicking on container)
      // =====================================================
      else if (browseFlag.equals(BrowseFlag.DIRECT_CHILDREN)) {
        // if BrowseFlag is set to "BrowseDirectChildren" , then TotalMatches shall be set to the total number of objects in the object specified
        // for the Browse() action (independent of the starting index specified by the StartingIndex argument) ( 5.5.8.2)

        if (path[0].equals(Upnp.ID_ROOT)) {
          int i = 0;

          StorageFolder cont = new StorageFolder();
          cont.setId(Upnp.ID_MOVIES);
          cont.setParentID(Upnp.ID_ROOT);
          cont.setTitle(BUNDLE.getString("tmm.movies"));
          cont.setChildCount(MovieList.getInstance().getMovieCount());
          didl.addContainer(cont);
          i++;

          if (maxResults == 0 || i < maxResults) {
            cont = new StorageFolder();
            cont.setId(Upnp.ID_TVSHOWS);
            cont.setParentID(Upnp.ID_ROOT);
            cont.setTitle(BUNDLE.getString("tmm.tvshows"));
            cont.setChildCount(TvShowList.getInstance().getTvShowCount());
            didl.addContainer(cont);
            i++;
          }

          return returnResult(didl);
        }
        else if (path[0].equals(Upnp.ID_MOVIES)) {
          // children of item is always empty
          if (path.length == 1) {
            // create MOVIE folder structure -> items
            List<org.tinymediamanager.core.movie.entities.Movie> tmmMovies = MovieList.getInstance().getMovies();
            Collections.sort(tmmMovies, new DynaComparator(orderMovie));
            int i = 0;
            for (org.tinymediamanager.core.movie.entities.Movie m : tmmMovies) {
              if (firstResult > 0) {
                firstResult--;
                continue;
              }
              if (maxResults == 0 || i < maxResults) {
                didl.addItem(Metadata.getUpnpMovie(m, false));
                i++;
              }
            }
          }
          return returnResult(didl);
        }
        else if (path[0].equals(Upnp.ID_TVSHOWS)) {
          if (path.length == 1) {
            // create TVSHOW folder structure -> container
            StorageFolder cont;
            List<org.tinymediamanager.core.tvshow.entities.TvShow> tmmShows = TvShowList.getInstance().getTvShows();
            Collections.sort(tmmShows, new DynaComparator(orderShow));
            int i = 0;
            for (org.tinymediamanager.core.tvshow.entities.TvShow t : tmmShows) {
              if (firstResult > 0) {
                firstResult--;
                continue;
              }
              if (maxResults == 0 || i < maxResults) {
                cont = new StorageFolder();
                cont.setId(Upnp.ID_TVSHOWS + "/" + t.getDbId());
                cont.setParentID(Upnp.ID_ROOT);
                cont.setTitle(t.getTitle());
                cont.setChildCount(t.getEpisodeCount());
                didl.addContainer(cont);
              }
              i++;
            }
          }
          else if (path.length == 2) {
            // create EPISODE items
            UUID uuid = UUID.fromString(path[1]);
            org.tinymediamanager.core.tvshow.entities.TvShow show = TvShowList.getInstance().lookupTvShow(uuid);
            if (show != null) {
              int i = 0;
              for (TvShowEpisode ep : show.getEpisodes()) {
                if (firstResult > 0) {
                  firstResult--;
                  continue;
                }
                if (maxResults == 0 || i < maxResults) {
                  didl.addItem(Metadata.getUpnpTvShowEpisode(show, ep, false));
                }
                i++;
              }
            }
          }
          return returnResult(didl);
        }
        else {
          LOGGER.warn("Whoops. There was an error in our directory structure. " + objectID);
        }
      }
      throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, "BrowseFlag wrong " + browseFlag);
    }
    catch (Exception ex) {
      LOGGER.error("Browse failed", ex);
      throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, ex.toString());
    }
  }

  private BrowseResult returnResult(DIDLContent didl) throws Exception {
    DIDLParser dip = new DIDLParser();
    int count = didl.getItems().size() + didl.getContainers().size();
    String ret = dip.generate(didl);
    LOGGER.debug(prettyFormat(ret, 2));
    return new BrowseResult(ret, count, count);
  }

  private int getInt(String s) {
    int i = 0;
    try {
      i = Integer.valueOf(s);
    }
    catch (NumberFormatException nfe) {
      LOGGER.warn("Cannot parse number from " + s);
    }
    return i;
  }

  @Override
  public BrowseResult search(String containerId, String searchCriteria, String filter, long firstResult, long maxResults, SortCriterion[] orderBy)
      throws ContentDirectoryException {
    // You can override this method to implement searching!
    return super.search(containerId, searchCriteria, filter, firstResult, maxResults, orderBy);
  }

  public static String prettyFormat(String input, int indent) {
    try {
      Source xmlInput = new StreamSource(new StringReader(input));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", indent);
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    }
    catch (Exception e) {
      return "! error parsing xml !";
    }
  }
}
