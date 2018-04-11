package life.qbic.connection.database.projectInvestigatorDB;

/**
 * Created by sven on 12/18/16.
 */
public class WrongArgumentSettingsException extends Exception {

  public WrongArgumentSettingsException() {
    super();
  }

  public WrongArgumentSettingsException(String s) {
    super(s);
  }

  public WrongArgumentSettingsException(String s, Throwable t) {
    super(s, t);
  }

}
