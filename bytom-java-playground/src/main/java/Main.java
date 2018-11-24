import io.bytom.api.*;
import io.bytom.common.*;
import io.bytom.http.*;
import io.bytom.exception.*;

class Main {
  public static void main(String[] argv) {
    System.out.println("Generating client");
    try {
      Client client = Client.generateClient();
      String alias = Configuration.getValue("client.access.user");
      String pass = Configuration.getValue("client.access.pass");
      Key.Builder builder = new Key.Builder().setAlias(alias).setPassword(pass);
      Key key = Key.create(client, builder);
      System.out.println(key.toJson());
    } catch (BytomException e) {
      e.printStackTrace();
    }
    System.out.println("Exiting");
  }

  public static Client generateClient() throws BytomException {
    String coreURL = Configuration.getValue("bytom.api.url");
    String accessToken = Configuration.getValue("client.access.token");
    if (coreURL == null || coreURL.isEmpty()) {
      coreURL = "http://127.0.0.1:9888/";
    }
    return new Client(coreURL, accessToken);
  }
}
