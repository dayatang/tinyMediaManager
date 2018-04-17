/*
 * Copyright 2012 - 2018 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinymediamanager.ui.moviesets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.AbstractModelObject;
import org.tinymediamanager.core.movie.MovieList;
import org.tinymediamanager.core.movie.MovieModuleManager;
import org.tinymediamanager.core.movie.MovieScraperMetadataConfig;
import org.tinymediamanager.core.movie.entities.Movie;
import org.tinymediamanager.core.movie.entities.MovieSet;
import org.tinymediamanager.core.threading.TmmTask;
import org.tinymediamanager.core.threading.TmmTaskManager;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaScrapeOptions;
import org.tinymediamanager.scraper.MediaScraper;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.ScraperType;
import org.tinymediamanager.scraper.entities.MediaArtwork;
import org.tinymediamanager.scraper.entities.MediaArtwork.MediaArtworkType;
import org.tinymediamanager.scraper.entities.MediaType;
import org.tinymediamanager.scraper.mediaprovider.IMovieArtworkProvider;
import org.tinymediamanager.scraper.mediaprovider.IMovieSetMetadataProvider;
import org.tinymediamanager.ui.UTF8Control;

/**
 * The Class MovieSetChooserModel.
 */
public class MovieSetChooserModel extends AbstractModelObject {
  private static final ResourceBundle      BUNDLE      = ResourceBundle.getBundle("messages", new UTF8Control());          //$NON-NLS-1$
  private static final Logger              LOGGER      = LoggerFactory.getLogger(MovieSetChooserModel.class);
  public static final MovieSetChooserModel emptyResult = new MovieSetChooserModel();
  private String                           name        = "";
  private String                           posterUrl   = "";
  private String                           fanartUrl   = "";
  private int                              tmdbId      = 0;
  private MediaSearchResult                result      = null;
  private MediaMetadata                    metadata    = null;
  private List<MovieInSet>                 movies      = ObservableCollections.observableList(new ArrayList<MovieInSet>());
  private MediaScraper                     scraper;

  private boolean                          scraped;

  public MovieSetChooserModel(MediaSearchResult result) {
    this.result = result;

    setName(result.getTitle());
    setTmdbId(Integer.parseInt(result.getId()));
    setPosterUrl(result.getPosterUrl());

    try {
      List<MediaScraper> sets = MediaScraper.getMediaScrapers(ScraperType.MOVIE_SET);
      if (sets != null && sets.size() > 0) {
        scraper = sets.get(0); // just get first
      }
    }
    catch (Exception e) {
      scraper = null;
    }
  }

  /**
   * create the empty search result.
   */
  private MovieSetChooserModel() {
    setName(BUNDLE.getString("chooser.nothingfound")); //$NON-NLS-1$
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    firePropertyChange("name", "", name);
  }

  public int getTmdbId() {
    return tmdbId;
  }

  public void setTmdbId(int tmdbId) {
    this.tmdbId = tmdbId;
  }

  public void setPosterUrl(String posterUrl) {
    this.posterUrl = posterUrl;
    firePropertyChange("posterUrl", "", posterUrl);
  }

  public void setFanartUrl(String fanartUrl) {
    this.fanartUrl = fanartUrl;
    firePropertyChange("fanartUrl", "", fanartUrl);
  }

  public boolean isScraped() {
    return scraped;
  }

  public String getPosterUrl() {
    return posterUrl;
  }

  public String getFanartUrl() {
    return fanartUrl;
  }

  /**
   * Match with existing movies.
   */
  public void matchWithExistingMovies() {
    List<Movie> moviesFromMovieList = MovieList.getInstance().getMovies();
    for (MovieInSet mis : movies) {
      // try to match via tmdbid
      if (mis.tmdbId > 0) {
        for (Movie movie : moviesFromMovieList) {
          if (movie.getTmdbId() == mis.tmdbId) {
            mis.setMovie(movie);
            break;
          }
        }
      }

      // try to match via imdbid if nothing has been found
      if (mis.movie == null) {
        if (StringUtils.isEmpty(mis.imdbId)) {
          // get imdbid for this tmdbid
          if (scraper.getMediaProvider() != null) {
            MediaScrapeOptions options = new MediaScrapeOptions(MediaType.MOVIE);
            options.setTmdbId(mis.tmdbId);
            options.setLanguage(LocaleUtils.toLocale(MovieModuleManager.MOVIE_SETTINGS.getScraperLanguage().name()));
            options.setCountry(MovieModuleManager.MOVIE_SETTINGS.getCertificationCountry());
            options.setFanartSize(MovieModuleManager.MOVIE_SETTINGS.getImageFanartSize());
            options.setPosterSize(MovieModuleManager.MOVIE_SETTINGS.getImagePosterSize());
            try {
              MediaMetadata md = ((IMovieSetMetadataProvider) scraper.getMediaProvider()).getMetadata(options);
              mis.imdbId = String.valueOf(md.getId(MediaMetadata.IMDB));
            }
            catch (Exception e) {
              LOGGER.warn(e.getMessage());
            }
          }
        }

        if (StringUtils.isNotEmpty(mis.imdbId)) {
          for (Movie movie : moviesFromMovieList) {
            if (mis.imdbId.equals(movie.getImdbId())) {
              mis.setMovie(movie);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * Scrape metadata.
   */
  public void scrapeMetadata() {
    try {
      if (scraper.getMediaProvider() != null) {
        MediaScrapeOptions options = new MediaScrapeOptions(MediaType.MOVIE_SET);
        options.setTmdbId(Integer.parseInt(result.getId()));
        options.setLanguage(LocaleUtils.toLocale(MovieModuleManager.MOVIE_SETTINGS.getScraperLanguage().name()));
        options.setCountry(MovieModuleManager.MOVIE_SETTINGS.getCertificationCountry());

        MediaMetadata info = ((IMovieSetMetadataProvider) scraper.getMediaProvider()).getMetadata(options);
        // if (info != null && StringUtils.isNotBlank(info.getTitle())) {
        // movieSet.setTitle(info.getTitle());
        // movieSet.setPlot(info.getPlot());
        // movieSet.setArtworkUrl(info.getPosterUrl(), MediaFileType.POSTER);
        // movieSet.setArtworkUrl(info.getFanartUrl(), MediaFileType.FANART);
        // }
        if (info != null) {
          this.metadata = info;
          if (!info.getMediaArt(MediaArtworkType.BACKGROUND).isEmpty()) {
            setFanartUrl(info.getMediaArt(MediaArtworkType.BACKGROUND).get(0).getDefaultUrl());
          }
          for (MediaMetadata item : info.getSubItems()) {
            MovieInSet movie = new MovieInSet(item.getTitle());
            try {
              movie.setTmdbId(Integer.parseInt(item.getId(MediaMetadata.TMDB).toString()));
            }
            catch (NumberFormatException ignored) {
            }
            if (item.getReleaseDate() != null) {
              movie.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd").format(item.getReleaseDate()));
            }
            movies.add(movie);
          }

          Collections.sort(movies);

          // try to match movies
          matchWithExistingMovies();

          this.scraped = true;
        }
      }
    }
    catch (Exception e) {
      LOGGER.warn("error while scraping metadata", e);
    }

  }

  public String getOverview() {
    if (metadata == null) {
      return null;
    }
    return metadata.getPlot();
  }

  public List<MovieInSet> getMovies() {
    return movies;
  }

  public void startArtworkScrapeTask(MovieSet movieSet, MovieScraperMetadataConfig config) {
    TmmTaskManager.getInstance().addUnnamedTask(new ArtworkScrapeTask(movieSet, config));
  }

  private class ArtworkScrapeTask extends TmmTask {
    private MovieSet                   movieSetToScrape;
    private MovieScraperMetadataConfig config;

    public ArtworkScrapeTask(MovieSet movieSet, MovieScraperMetadataConfig config) {
      super(BUNDLE.getString("message.scrape.artwork") + " " + movieSet.getTitle(), 0, TaskType.BACKGROUND_TASK);
      this.movieSetToScrape = movieSet;
      this.config = config;
    }

    @Override
    protected void doInBackground() {
      if (!scraped) {
        return;
      }

      List<MediaArtwork> artwork = new ArrayList<>();

      MediaScrapeOptions options = new MediaScrapeOptions(MediaType.MOVIE);
      options.setArtworkType(MediaArtwork.MediaArtworkType.ALL);
      options.setMetadata(metadata);
      options.setId(MediaMetadata.IMDB, String.valueOf(metadata.getId(MediaMetadata.IMDB)));
      try {
        options.setTmdbId(Integer.parseInt(String.valueOf(metadata.getId(MediaMetadata.TMDB_SET))));
      }
      catch (Exception e) {
        options.setTmdbId(0);
      }
      options.setLanguage(LocaleUtils.toLocale(MovieModuleManager.MOVIE_SETTINGS.getScraperLanguage().name()));
      options.setCountry(MovieModuleManager.MOVIE_SETTINGS.getCertificationCountry());
      options.setFanartSize(MovieModuleManager.MOVIE_SETTINGS.getImageFanartSize());
      options.setPosterSize(MovieModuleManager.MOVIE_SETTINGS.getImagePosterSize());

      // scrape providers till one artwork has been found
      for (MediaScraper artworkScraper : MovieList.getInstance().getDefaultArtworkScrapers()) {
        IMovieArtworkProvider artworkProvider = (IMovieArtworkProvider) artworkScraper.getMediaProvider();
        try {
          artwork.addAll(artworkProvider.getArtwork(options));
        }
        catch (Exception e) {
        }
      }

      // at last take the poster from the result
      if (StringUtils.isNotBlank(getPosterUrl())) {
        MediaArtwork ma = new MediaArtwork(result.getProviderId(), MediaArtwork.MediaArtworkType.POSTER);
        ma.setDefaultUrl(getPosterUrl());
        ma.setPreviewUrl(getPosterUrl());
        artwork.add(ma);
      }

      movieSetToScrape.setArtwork(artwork, config);
    }
  }

  public static class MovieInSet extends AbstractModelObject implements Comparable<MovieInSet> {
    private String name        = "";
    private int    tmdbId      = 0;
    private String imdbId      = "";
    private String releaseDate = "";
    private Movie  movie       = null;

    public MovieInSet(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public int getTmdbId() {
      return tmdbId;
    }

    public String getImdbId() {
      return imdbId;
    }

    public String getReleaseDate() {
      return releaseDate;
    }

    public Movie getMovie() {
      return movie;
    }

    public void setTmdbId(int tmdbId) {
      this.tmdbId = tmdbId;
    }

    public void setImdbId(String imdbId) {
      this.imdbId = imdbId;
    }

    public void setReleaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
    }

    public void setMovie(Movie movie) {
      this.movie = movie;
      firePropertyChange("movie", null, movie);
    }

    @Override
    public int compareTo(MovieInSet o) {
      return releaseDate.compareTo(o.releaseDate);
    }
  }
}
