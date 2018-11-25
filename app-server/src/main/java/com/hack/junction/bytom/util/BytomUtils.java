package com.hack.junction.bytom.util;

import io.bytom.api.*;
import io.bytom.common.*;
import io.bytom.http.*;
import io.bytom.exception.*;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;

import org.apache.commons.codec.binary.Base64;

import org.json.*;

public class BytomUtils {
  public static void testUserCreating() throws BytomException {
    Map<String, String> res = createNewKeyAndUser("test3", "qwerty");
    System.out.println(res.toString());
  }

  public static void testTransfering() throws BytomException {
    Client client = Client.generateClient();
    String fromAcc = Configuration.getValue("escrow.sender.id");
    String toAddress = Configuration.getValue("escrow.receiver.address");
    String assetId = Configuration.getValue("asset.sct.id");
    long amount = 10000000;
    long gasAmount = 100000000;
    transferAsset(client, fromAcc, toAddress, assetId, amount, gasAmount);
  }

  public static void processTransaction(String agent, String senderAccount, String senderProgram, String receiverProgram, String receiverAddress, long amount) throws Exception {
      String program = compileContract(agent, senderProgram, receiverProgram);
      String txid = lockContract(program, senderAccount, receiverAddress, amount, amount * 100);
      java.util.concurrent.TimeUnit.MINUTES.sleep(3);
      System.out.println("TXID: " + txid);
      unlockContract(txid, senderAccount, receiverProgram, amount, amount * 100);
  }

  public static String lockContract(String program, String sender, String reciever, long amount, long fee) throws Exception {
    URL url = new URL("http://localhost:9888/build-transaction");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty ("Authorization", getAuth());

    StringBuilder builder = new StringBuilder();
    builder.append("{\n");
    builder.append("\"base_transaction\":null,\n");
    builder.append("\"ttl\":90000,\n");
    builder.append("\"actions\": [\n");

    builder.append("{");
    builder.append("\"account_id\":\"" + sender + "\",\n");
    builder.append("\"amount\":" + fee + ",\n");
    builder.append("\"asset_id\":\"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\",\n");
    builder.append("\"type\":\"spend_account\"\n");
    builder.append("},\n");

    builder.append("{");
    builder.append("\"account_id\":\"" + sender + "\",\n");
    builder.append("\"amount\":" + amount + ",\n");
    builder.append("\"asset_id\":\"" + Configuration.getValue("asset.sct.id") + "\",\n");
    builder.append("\"type\":\"spend_account\"\n");
    builder.append("},\n");

    builder.append("{");
    builder.append("\"control_program\":\"" + program + "\",\n");
    builder.append("\"amount\":" + amount + ",\n");
    builder.append("\"asset_id\":\"" + Configuration.getValue("asset.sct.id") + "\",\n");
    builder.append("\"type\":\"control_program\"\n");
    builder.append("}\n");

    builder.append("]\n");
    builder.append("}\n");

    connection.setUseCaches(false);
    connection.setDoOutput(true);

    try (OutputStream output = connection.getOutputStream()) {
      output.write(builder.toString().getBytes());
    }

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder responseBuilder = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      responseBuilder.append(inputLine);
    }
    in.close();
    System.out.println(responseBuilder.toString());

    JSONObject obj = new JSONObject(responseBuilder.toString());
    Client client = Client.generateClient();
    Transaction.Template transaction = Utils.serializer.fromJson(obj.getJSONObject("data").toString(), Transaction.Template.class);
    Transaction.Template signer = new Transaction.SignerBuilder().sign(client, transaction, Configuration.getValue("security.password"));
    Transaction.SubmitResponse txs = Transaction.submit(client, signer);
    return txs.tx_id;
  }

  public static void unlockContract(String txid, String sender, String receiverProgram, long amount, long fee) throws Exception {
    Client client = Client.generateClient();
    String outputId = "";
    Transaction trans = new Transaction.QueryBuilder().setTxId(txid).get(client);
    for (Transaction.Output output : trans.outputs) {
      if (output.address == null || output.address.isEmpty()) {
        outputId = output.id;
      }
    }
    if (outputId.equals("")) {
      return;
    }
    System.out.println("Output ID: " + outputId);

    URL url = new URL("http://localhost:9888/list-unspent-outputs");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty ("Authorization", getAuth());

    StringBuilder builder = new StringBuilder();
    builder.append("{\n");
    builder.append("\"smart_contract\":true,\n");
    builder.append("\"id\":\"" + outputId + "\"\n");
    builder.append("}\n");

    connection.setUseCaches(false);
    connection.setDoOutput(true);

    try (OutputStream output = connection.getOutputStream()) {
      output.write(builder.toString().getBytes());
    }

    System.out.println(builder.toString());

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder responseBuilder = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      responseBuilder.append(inputLine);
    }
    in.close();
    System.out.println(responseBuilder.toString());

    JSONObject obj = new JSONObject(responseBuilder.toString());
 
    String sourceId = obj.getJSONArray("data").getJSONObject(0).getString("source_id");

    url = new URL("http://localhost:9888/build-transaction");
    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty ("Authorization", getAuth());

    builder = new StringBuilder();
    builder.append("{\n");
    builder.append("\"base_transaction\":null,\n");
    builder.append("\"ttl\":90000,\n");
    builder.append("\"actions\": [\n");

    builder.append("{");
    builder.append("\"type\":\"spend_account_unspent_output\",\n");

      builder.append("\"output_id\":\"" + outputId + "\",\n");
      builder.append("\"arguments\": [\n");
      builder.append("{");
      builder.append("\"type\":\"raw_tx_signature\",\n");
      builder.append("\"raw_data\": { \"xpub\":\"" + Configuration.getValue("escrow.agent.xpub") + "\", \"derivation_path\": [\"010100000000000000\",\"0100000000000000\"] } \n");
      builder.append("},\n");
      builder.append("{");
      builder.append("\"type\":\"integer\",\n");
      builder.append("\"raw_data\": { \"value\":0 } \n");
      builder.append("}\n");
      builder.append("]\n");

    builder.append("},\n");

    builder.append("{");
    builder.append("\"control_program\":\"" + sourceId + "\",\n");
    builder.append("\"amount\":" + amount + ",\n");
    builder.append("\"asset_id\":\"" + Configuration.getValue("asset.sct.id") + "\",\n");
    builder.append("\"type\":\"control_program\"\n");
    builder.append("},\n");

    builder.append("{");
    builder.append("\"account_id\":\"" + sender + "\",\n");
    // builder.append("\"amount\":" + fee + ",\n");
    builder.append("\"amount\":" + 40000000 + ",\n");
    builder.append("\"asset_id\":\"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\",\n");
    builder.append("\"type\":\"spend_account\"\n");
    builder.append("}\n");

    builder.append("]\n");
    builder.append("}\n");

    connection.setUseCaches(false);
    connection.setDoOutput(true);

    try (OutputStream output = connection.getOutputStream()) {
      output.write(builder.toString().getBytes());
    }

    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    responseBuilder = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      responseBuilder.append(inputLine);
    }
    in.close();
    System.out.println(responseBuilder.toString());

    obj = new JSONObject(responseBuilder.toString());
    Transaction.Template transaction = Utils.serializer.fromJson(obj.getJSONObject("data").toString(), Transaction.Template.class);
    Transaction.Template signer = new Transaction.SignerBuilder().sign(client, transaction, Configuration.getValue("security.password"));
    Transaction.SubmitResponse txs = Transaction.submit(client, signer);
  }

  public static String compileContract(String agent, String sender, String recipient) throws Exception {
    URL url = new URL("http://localhost:9888/compile");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");

    StringBuilder builder = new StringBuilder();
    builder.append("{\n");
    builder.append("\"contract\": \"contract Escrow(agent: PublicKey,sender: Program,recipient: Program) locks valueAmount of valueAsset {clause approve(sig: Signature) {verify checkTxSig(agent, sig)lock valueAmount of valueAsset with recipient}clause reject(sig: Signature) {verify checkTxSig(agent, sig)lock valueAmount of valueAsset with sender}} \",\n");
    builder.append("\"args\": [\n");

    builder.append("{\"string\": \"");
    builder.append(agent);
    builder.append("\"},\n");

    builder.append("{\"string\": \"");
    builder.append(sender);
    builder.append("\"},\n");

    builder.append("{\"string\": \"");
    builder.append(recipient);
    builder.append("\"}\n");

    builder.append("]\n");
    builder.append("}\n");

    connection.setUseCaches(false);
    connection.setDoOutput(true);

    try (OutputStream output = connection.getOutputStream()) {
      output.write(builder.toString().getBytes());
    }

    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String inputLine;
    StringBuilder responseBuilder = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      responseBuilder.append(inputLine);
    }
    in.close();

    JSONObject obj = new JSONObject(responseBuilder.toString());
    String result = obj.getJSONObject("data").getString("program");
    return result;
  }

  public static void transferAsset(Client client, String from, String to, String assetId, long amount, long gasAmount) throws BytomException {
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
      .setTtl(1000)
      .build(client);

    Transaction.Template signer = new Transaction.SignerBuilder().sign(client, transaction, Configuration.getValue("security.password"));
    Transaction.SubmitResponse txs = Transaction.submit(client, signer);
  }

  public static Map<String, String> createNewKeyAndUser(String alias, String password) {
    Map<String, String> result = new HashMap<>();
    try {
      Client client = Client.generateClient();
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
    } catch (BytomException e) {
      e.printStackTrace();
    } finally {
      return result;
    }
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

  public static String getAuth() {
    byte[] encodedAuth = Base64.encodeBase64(Configuration.getValue("client.access.token").getBytes(Charset.forName("US-ASCII")));
    String auth = "Basic " + new String(encodedAuth);
    return auth;
  }
}
