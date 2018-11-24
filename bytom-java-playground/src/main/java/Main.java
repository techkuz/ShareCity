import io.bytom.api.*;
import io.bytom.common.*;
import io.bytom.http.*;
import io.bytom.exception.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

class Main {
  public static void main(String[] argv) {
    System.out.println("Generating client");
    try {
      Client client = Client.generateClient();
      String fromAcc = Configuration.getValue("escrow.sender.id");
      String toAddress = Configuration.getValue("escrow.receiver.address");
      String assetId = Configuration.getValue("asset.sct.id");
      int amount = 100;
      int gasAmount = 10000000;
      transferAsset(client, fromAcc, toAddress, assetId, amount, gasAmount);
    } catch (BytomException e) {
      e.printStackTrace();
    }
    System.out.println("Exiting");
  }

  public static void transferAsset(Client client, String from, String to, String assetId, int amount, int gasAmount) throws BytomException {
    Transaction.Template transaction = new Transaction.Builder()
      .addAction(new Transaction.Action.SpendFromAccount()
                      .setAccountId(from)
                      .setAssetId(assetId)
                      .setAmount(amount))
      .addAction(new Transaction.Action.ControlWithAddress()
                      .setAddress(to)
                      .setAssetId(assetId)
                      .setAmount(amount))
      .addAction(new Transaction.Action.SpendFromAccount()
                      .setAccountId(from)
                      .setAssetId(Configuration.getValue("asset.btm.id"))
                      .setAmount(gasAmount))
      .build(client);

    Transaction.Template signer = new Transaction.SignerBuilder().sign(client, transaction, Configuration.getValue("security.password"));
    Transaction.SubmitResponse txs = Transaction.submit(client, signer);
  }

  public static Map<String, String> createNewKeyAndUser(Client client, String alias, String password) throws BytomException {
    Map<String, String> result = new HashMap<>();
    Key.Builder keyBuilder = new Key.Builder().setAlias(alias).setPassword(password);
    Key key = Key.create(client, keyBuilder);
    result.put("xpub", key.xpub);
    result.put("mnemonic", key.xpub);

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

  public static Map<String, Receiver> getEscrowTestAccounts(Client client) throws BytomException {
    Map<String, Receiver> result = new HashMap<>();
    result.put("sender", getReceiver(client, Configuration.getValue("escrow.sender.alias"), Configuration.getValue("escrow.sender.id")));
    result.put("receiver", getReceiver(client, Configuration.getValue("escrow.receiver.alias"), Configuration.getValue("escrow.receiver.id")));
    result.put("agent", getReceiver(client, Configuration.getValue("escrow.agent.alias"), Configuration.getValue("escrow.agent.id")));
    return result;
  }

  public static Receiver getReceiver(Client client, String alias, String id) throws BytomException {
    Account.ReceiverBuilder builder = new Account.ReceiverBuilder().setAccountAlias(alias).setAccountId(id);
    return builder.create(client);
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
