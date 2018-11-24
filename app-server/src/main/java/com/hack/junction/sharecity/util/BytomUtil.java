import io.bytom.api.*;
import io.bytom.exception.*;
import io.bytom.http.*;
import io.bytom.common.*;

import java.util.*;

class BytomUtil {
  public static Map<String, String> createNewKeyAndUser(String alias, String password) throws BytomException {
    Client client = Client.generateClient();
    Map<String, String> result = new HashMap<>();
    Key.Builder keyBuilder = new Key.Builder().setAlias(alias).setPassword(password);
    Key key = Key.create(client, keyBuilder);
    result.put("xpub", key.xpub);

    List<String> rootXpubs = new ArrayList<>();
    rootXpubs.add(key.xpub);

    Account.Builder accBuilder = new Account.Builder().setAlias(alias).setQuorum(1).setRootXpub(rootXpubs);
    Account acc = Account.create(client, accBuilder);
    result.put("id", acc.id);

    Account.ReceiverBuilder receiverBuilder = new Account.ReceiverBuilder().setAccountAlias(alias).setAccountId(acc.id);
    Receiver receiver = receiverBuilder.create(client);

    result.put("address", receiver.address);
    result.put("control_program", receiver.controlProgram);

    return result;
  }
}
