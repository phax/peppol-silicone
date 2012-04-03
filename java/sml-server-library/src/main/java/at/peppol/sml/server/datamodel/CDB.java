package at.peppol.sml.server.datamodel;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class CDB {
  public static final int MAX_USERNAME_LENGTH = 200;
  public static final int MAX_SMPID_LENGTH = 200;
  public static final int MAX_MIGRATION_CODE_LENGTH = 200;
  public static final int MAX_PHYSICAL_ADDRESS_LENGTH = 65535;
  public static final int MAX_LOGICAL_ADDRESS_LENGTH = 65535;
  public static final int MAX_PASSWORD_LENGTH = 65535;

  private CDB () {}
}
