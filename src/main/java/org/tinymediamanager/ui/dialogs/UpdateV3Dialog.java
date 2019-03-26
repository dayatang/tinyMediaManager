/*
 * Copyright 2012 - 2019 Manuel Laggner
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
package org.tinymediamanager.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.Message;
import org.tinymediamanager.core.Message.MessageLevel;
import org.tinymediamanager.core.MessageManager;
import org.tinymediamanager.ui.EqualsLayout;
import org.tinymediamanager.ui.TmmFontHelper;
import org.tinymediamanager.ui.TmmUIHelper;
import org.tinymediamanager.ui.UTF8Control;
import org.tinymediamanager.ui.components.LinkLabel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

/**
 * The class UpdateDialog. Used to show the user that an update is available
 * 
 * @author Manuel Laggner
 */
public class UpdateV3Dialog extends TmmDialog {
  private static final long           serialVersionUID = 535315282932742179L;
  /**
   * @wbp.nls.resourceBundle messages
   */
  private static final ResourceBundle BUNDLE           = ResourceBundle.getBundle("messages", new UTF8Control()); //$NON-NLS-1$
  private static final Logger         LOGGER           = LoggerFactory.getLogger(UpdateV3Dialog.class);

  public UpdateV3Dialog() {
    super(BUNDLE.getString("tmm.update.title"), "update"); //$NON-NLS-1$
    setSize(705, 503);
    {
      JPanel panel = new JPanel();
      getContentPane().add(panel, BorderLayout.CENTER);
      panel.setLayout(new FormLayout(
          new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("10dlu"), FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("10dlu"),
              FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("300dlu:grow"),
              FormSpecs.RELATED_GAP_COLSPEC, },
          new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.PARAGRAPH_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("5dlu"),
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("15dlu"), FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
              FormSpecs.RELATED_GAP_ROWSPEC, }));

      JLabel lblUpdateInfo = new JLabel();
      lblUpdateInfo.setText("tinyMediaManager v3 is available");
      TmmFontHelper.changeFont(lblUpdateInfo, 1.33, Font.BOLD);
      panel.add(lblUpdateInfo, "6, 2, 3, 1, center, default");

      JLabel lblChangelog = new JLabel(BUNDLE.getString("whatsnew.title")); //$NON-NLS-1$
      TmmFontHelper.changeFont(lblChangelog, 1.16, Font.BOLD);
      panel.add(lblChangelog, "2, 4, 5, 1, fill, default");
      {
        JLabel lblScreenshot = new JLabel(new ImageIcon(UpdateV3Dialog.class.getResource("/v3.png")));

        panel.add(lblScreenshot, "8, 6, 1, 41, center, center");
      }
      {
        JLabel lblNewLabel = new JLabel("completely rewritten UI");
        panel.add(lblNewLabel, "4, 6, 3, 1");
      }
      {
        JLabel lblNewLabel_1 = new JLabel("new style and layout (better usage of the available space especially for low screen devices)");
        panel.add(lblNewLabel_1, "6, 8");
      }
      {
        JLabel lblNewLabel_2 = new JLabel("flexible and configurable tables");
        panel.add(lblNewLabel_2, "6, 10");
      }
      {
        JLabel lblNewLabel_3 = new JLabel("improved filters (inclusive and exclusive filters)");
        panel.add(lblNewLabel_3, "6, 12");
      }
      {
        JLabel lblNewLabel_4 = new JLabel("better UI scaling on high dpi screens");
        panel.add(lblNewLabel_4, "6, 14");
      }
      {
        JLabel lblNewLabel_5 = new JLabel("added a dark theme");
        panel.add(lblNewLabel_5, "4, 16, 3, 1");
      }
      {
        JLabel lblNewLabel_6 = new JLabel("completely rewritten the renamer engine");
        panel.add(lblNewLabel_6, "4, 18, 3, 1");
      }
      {
        JLabel lblNewLabel_7 = new JLabel("completely rewritten NFO parsing and writing (much more flexible now)");
        panel.add(lblNewLabel_7, "4, 20, 3, 1");
      }
      {
        JLabel lblNewLabel_11 = new JLabel("ability to mix in missing episodes");
        panel.add(lblNewLabel_11, "4, 22, 3, 1");
      }
      {
        JLabel lblNewLabel_12 = new JLabel("presets in settings for common media centers");
        panel.add(lblNewLabel_12, "4, 24, 3, 1");
      }
      {
        JLabel lblNewLabel_8 = new JLabel("easier translation via weblate.org");
        panel.add(lblNewLabel_8, "4, 26, 3, 1");
      }
      {
        JLabel lblNewLabel_9 = new JLabel("increased required Java version to Java 8+");
        panel.add(lblNewLabel_9, "4, 28, 3, 1");
      }
      {
        JLabel lblNewLabel_10 = new JLabel("many enhancements under the hood");
        panel.add(lblNewLabel_10, "4, 30, 3, 1");
      }
      {
        JLabel lblNewLabel_22 = new JLabel("More infos at");
        panel.add(lblNewLabel_22, "4, 34, 3, 1");
      }
      {
        final LinkLabel linkLabel = new LinkLabel("http://www.tinymediamanager.org/");
        linkLabel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            try {
              TmmUIHelper.browseUrl(linkLabel.getNormalText());
            }
            catch (Exception e) {
              LOGGER.error(e.getMessage());
              MessageManager.instance.pushMessage(
                  new Message(MessageLevel.ERROR, linkLabel.getNormalText(), "message.erroropenurl", new String[] { ":", e.getLocalizedMessage() }));
            }
          }
        });
        panel.add(linkLabel, "6, 36");
      }
      {
        JLabel lblNewLabel_13 = new JLabel("Update info");
        TmmFontHelper.changeFont(lblNewLabel_13, 1.16, Font.BOLD);
        panel.add(lblNewLabel_13, "2, 40, 5, 1");
      }
      {
        JLabel lblNewLabel_14 = new JLabel("A direct update to v3 is not possible since too many things changed.");
        panel.add(lblNewLabel_14, "4, 42, 3, 1");
      }
      {
        JLabel lblNewLabel_15 = new JLabel("By pressing the \"Update\" button the following steps will be processed");
        panel.add(lblNewLabel_15, "4, 44, 3, 1");
      }
      {
        JLabel lblNewLabel_16 = new JLabel("a) your tinyMediaManager database will be deleted");
        panel.add(lblNewLabel_16, "6, 46");
      }
      {
        JLabel lblNewLabel_17 = new JLabel("b) the settings for data sources will be exported");
        panel.add(lblNewLabel_17, "6, 48");
      }
      {
        JLabel lblNewLabel_18 = new JLabel("c) tmm will be upgraded to v3");
        panel.add(lblNewLabel_18, "6, 50");
      }
      {
        JLabel lblNewLabel_19 = new JLabel("d) the settings for your data sources will be imported into v3");
        panel.add(lblNewLabel_19, "6, 52");
      }
      {
        JLabel lblNewLabel_20 = new JLabel("e) an automatic \"update datasources\" will be performed");
        panel.add(lblNewLabel_20, "6, 54");
      }
      {
        JLabel lblNewLabel_21 = new JLabel("Unfortunately you have to make all your settings in v3 too since we're not able to migrate them");
        panel.add(lblNewLabel_21, "4, 56, 5, 1");
      }
    }
    {
      JPanel panel = new JPanel();
      getContentPane().add(panel, BorderLayout.SOUTH);
      panel.setLayout(new FormLayout(
          new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC,
              ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, },
          new RowSpec[] { FormSpecs.LINE_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, }));
      {
        JPanel panel_1 = new JPanel();
        EqualsLayout layout = new EqualsLayout(5);
        layout.setMinWidth(100);
        panel_1.setLayout(layout);
        panel.add(panel_1, "2, 2, fill, fill");

        JButton btnUpdate = new JButton(BUNDLE.getString("Button.update"));
        panel_1.add(btnUpdate, "1, 1");
        btnUpdate.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            setVisible(false);
            LOGGER.info("Updating...");

            // FIXME: start upgrade
          }
        });
      }

      JPanel buttonPanel = new JPanel();
      EqualsLayout layout = new EqualsLayout(5);
      layout.setMinWidth(100);
      buttonPanel.setLayout(layout);
      panel.add(buttonPanel, "6, 2");

      JButton btnClose = new JButton(BUNDLE.getString("Button.close")); //$NON-NLS-1$
      getRootPane().setDefaultButton(btnClose);
      btnClose.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          setVisible(false);
        }
      });
      buttonPanel.add(btnClose);
    }

  }

  @Override
  public Dimension getPreferredSize() {
    Dimension superPref = super.getPreferredSize();
    return new Dimension((int) (700 > superPref.getWidth() ? superPref.getWidth() : 700),
        (int) (500 > superPref.getHeight() ? superPref.getHeight() : 500));
  }

  private String prepareTextAsHtml(String originalText) {
    Pattern pattern = Pattern.compile("(http[s]?://.*?)[ )]");
    Matcher matcher = pattern.matcher(originalText);
    while (matcher.find()) {
      originalText = originalText.replace(matcher.group(1), "<a href=\"" + matcher.group(1) + "\">" + matcher.group(1) + "</a>");
    }

    return "<html><pre>" + originalText + "</pre><html>";
  }
}
