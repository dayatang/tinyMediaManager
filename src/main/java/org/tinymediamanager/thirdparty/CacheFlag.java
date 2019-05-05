package org.tinymediamanager.thirdparty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.Utils;

/**
 * A cache flag<br>
 * you can check if flag has been exceeded (starting once per day)<br>
 * which get successively increased day by day...
 * 
 * 
 * 
 * @author Myron Boyle
 *
 */
public class CacheFlag {
  private static final Logger LOGGER        = LoggerFactory.getLogger(CacheFlag.class);
  static final private long   ONE_DAY_IN_MS = 24 * 60 * 60 * 1000;
  private Path                cacheFile     = null;
  int                         days          = 0;
  private long                filetime      = System.currentTimeMillis();

  // workflow
  // 1. assign cache flag "file"
  // 2. if filedate exceeds X days, bring a messagebox
  // 3. if answer is NO, increment "days" - so it gets visible in a stretched period (to not be annoing)

  // @formatter:off
  //  CacheFlag cf = new CacheFlag(Paths.get("cache", "migv3.popup"));
  //  if (cf.exceeded()) {
  //    int answer = JOptionPane.showConfirmDialog(null, "Wanna try out V3", "Migration", JOptionPane.YES_NO_OPTION);
  //    if (answer == JOptionPane.NO_OPTION) {
  //      // try again in a few days
  //      cf.increment();
  //    }
  //  }
  // @formatter:on

  private CacheFlag() {
  }

  public CacheFlag(Path file, int initial) {
    cacheFile = file;
    days = initial;
    if (Files.exists(cacheFile)) {
      try {
        String cont = Utils.readFileToString(cacheFile);
        days = Integer.parseInt(cont);
        filetime = Files.getLastModifiedTime(cacheFile).toMillis();
      }
      catch (Exception e) {
        LOGGER.warn("Could not read/parse cache! {}", e);
        increment();
      }
    }
    else {
      increment();
    }
  }

  public boolean exceeded() {
    // is file modified longer that "days"?
    if (System.currentTimeMillis() - filetime > days * ONE_DAY_IN_MS) {
      return true;
    }
    return false;
  }

  public void increment() {
    try {
      days++;
      Utils.writeStringToFile(cacheFile, String.valueOf(days));
    }
    catch (IOException e) {
      LOGGER.warn("Could not write cache! {}", e);
    }
  }
}
