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
package org.tinymediamanager.ui.movies;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tinymediamanager.core.AbstractModelObject;
import org.tinymediamanager.core.movie.MovieSearchOptions;
import org.tinymediamanager.core.movie.entities.Movie;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;

/**
 * The Class MovieSelectionModel.
 * 
 * @author Manuel Laggner
 */
public class MovieSelectionModel extends AbstractModelObject implements ListSelectionListener {

  /** The Constant SELECTED_MOVIE. */
  private static final String               SELECTED_MOVIE = "selectedMovie";

  /** The selected movies. */
  private List<Movie>                       selectedMovies;

  /** The selected movie. */
  private Movie                             selectedMovie;

  /** The initial movie. */
  private Movie                             initialMovie   = new Movie();

  /** The selection model. */
  private DefaultEventSelectionModel<Movie> selectionModel;

  /** The matcher editor. */
  private MovieMatcherEditor                matcherEditor;

  /** The table comparator chooser. */
  private TableComparatorChooser<Movie>     tableComparatorChooser;

  /** The sorted list. */
  private SortedList<Movie>                 sortedList;

  /** The property change listener. */
  private PropertyChangeListener            propertyChangeListener;

  /**
   * Instantiates a new movie selection model. Usage in MoviePanel
   * 
   * @param sortedList
   *          the sorted list
   * @param source
   *          the source
   * @param matcher
   *          the matcher
   */
  public MovieSelectionModel(SortedList<Movie> sortedList, EventList<Movie> source, MovieMatcherEditor matcher) {
    this.sortedList = sortedList;
    this.selectionModel = new DefaultEventSelectionModel<>(source);
    this.selectionModel.addListSelectionListener(this);
    this.matcherEditor = matcher;
    this.selectedMovies = selectionModel.getSelected();

    propertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == selectedMovie) {
          firePropertyChange(evt);
        }
      }
    };
  }

  /**
   * Instantiates a new movie selection model. Usage in MovieSetPanel
   */
  public MovieSelectionModel() {

  }

  /**
   * Sets the selected movie.
   * 
   * @param movie
   *          the new selected movie
   */
  public void setSelectedMovie(Movie movie) {
    Movie oldValue = this.selectedMovie;
    if (movie == null) {
      this.selectedMovie = initialMovie;
    }
    else {
      this.selectedMovie = movie;
    }

    if (oldValue != null) {
      oldValue.removePropertyChangeListener(propertyChangeListener);
    }

    if (selectedMovie != null) {
      selectedMovie.addPropertyChangeListener(propertyChangeListener);
    }

    firePropertyChange(SELECTED_MOVIE, oldValue, selectedMovie);
  }

  /**
   * Gets the matcher editor.
   * 
   * @return the matcher editor
   */
  public MovieMatcherEditor getMatcherEditor() {
    return matcherEditor;
  }

  /**
   * Gets the selection model.
   * 
   * @return the selection model
   */
  public DefaultEventSelectionModel<Movie> getSelectionModel() {
    return selectionModel;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event. ListSelectionEvent)
   */
  /**
   * Value changed.
   * 
   * @param e
   *          the e
   */
  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      return;
    }

    // display first selected movie
    if (selectedMovies.size() > 0 && selectedMovie != selectedMovies.get(0)) {
      Movie oldValue = selectedMovie;
      selectedMovie = selectedMovies.get(0);

      // unregister propertychangelistener
      if (oldValue != null && oldValue != initialMovie) {
        oldValue.removePropertyChangeListener(propertyChangeListener);
      }
      if (selectedMovie != null && selectedMovie != initialMovie) {
        selectedMovie.addPropertyChangeListener(propertyChangeListener);
      }
      firePropertyChange(SELECTED_MOVIE, oldValue, selectedMovie);
    }

    // display empty movie (i.e. when all movies are removed from the list)
    if (selectedMovies.size() == 0) {
      Movie oldValue = selectedMovie;
      selectedMovie = initialMovie;
      // unregister propertychangelistener
      if (oldValue != null && oldValue != initialMovie) {
        oldValue.removePropertyChangeListener(propertyChangeListener);
      }
      firePropertyChange(SELECTED_MOVIE, oldValue, selectedMovie);
    }
  }

  /**
   * Gets the selected movie.
   * 
   * @return the selected movie
   */
  public Movie getSelectedMovie() {
    if (selectedMovie == null) {
      return initialMovie;
    }
    return selectedMovie;
  }

  /**
   * Gets the selected movies.
   * 
   * @return the selected movies
   */
  public List<Movie> getSelectedMovies() {
    return selectedMovies;
  }

  /**
   * Sets the selected movies.
   * 
   * @param selectedMovies
   *          the new selected movies
   */
  public void setSelectedMovies(List<Movie> selectedMovies) {
    this.selectedMovies = selectedMovies;
  }

  /**
   * Filter movies.
   * 
   * @param filter
   *          the filter
   */
  public void filterMovies(Map<MovieSearchOptions, Object> filter) {
    matcherEditor.filterMovies(filter);
    firePropertyChange("filterChanged", filter.isEmpty(), !filter.isEmpty());
  }

  /**
   * Gets the table comparator chooser.
   * 
   * @return the table comparator chooser
   */
  public TableComparatorChooser<Movie> getTableComparatorChooser() {
    return tableComparatorChooser;
  }

  /**
   * Sets the table comparator chooser.
   * 
   * @param tableComparatorChooser
   *          the new table comparator chooser
   */
  public void setTableComparatorChooser(TableComparatorChooser<Movie> tableComparatorChooser) {
    this.tableComparatorChooser = tableComparatorChooser;
  }

  /**
   * Sort movies.
   * 
   * @param column
   *          the column
   * @param ascending
   *          the ascending
   */
  public void sortMovies(MovieExtendedComparator.SortColumn column, boolean ascending) {
    Comparator<Movie> comparator = new MovieExtendedComparator(column, ascending);
    sortedList.setComparator(comparator);
  }
}
