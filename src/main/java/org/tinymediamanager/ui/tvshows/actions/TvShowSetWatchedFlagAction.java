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
package org.tinymediamanager.ui.tvshows.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import org.tinymediamanager.core.tvshow.entities.TvShowEpisode;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.tvshows.TvShowUIModule;

/**
 * The class TvShowSetWatchedFlagAction. Set the watched flag to all selected episodes
 * 
 * @author Manuel Laggner
 */
public class TvShowSetWatchedFlagAction extends AbstractAction {
  private static final long           serialVersionUID = 5762347331284295996L;
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$

  public TvShowSetWatchedFlagAction() {
    putValue(NAME, BUNDLE.getString("tvshowepisode.setwatchedflag")); //$NON-NLS-1$
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final List<TvShowEpisode> selectedEpisodes = TvShowUIModule.getInstance().getSelectionModel().getSelectedEpisodes();

    for (TvShowEpisode episode : selectedEpisodes) {
      episode.setWatched(true);
      episode.writeNFO();
      episode.saveToDb();
    }
  }
}
