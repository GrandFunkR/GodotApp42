//namespace is wrong, will eventually change
package com.android.godot;
//namespace is wrong, will eventually change


//import org.godotengine.godot.*;
import org.godotengine.godot.Godot;
import org.godotengine.godot.Godot.SingletonBase; 
import org.godotengine.godot.GodotLib;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import javax.microedition.khronos.opengles.GL10;
import com.shephertz.app42.paas.sdk.android.App42API;  
import com.shephertz.app42.paas.sdk.android.App42Response;  
import com.shephertz.app42.paas.sdk.android.App42Exception;  
import com.shephertz.app42.paas.sdk.android.App42BadParameterException;  
import com.shephertz.app42.paas.sdk.android.App42NotFoundException;  
import com.shephertz.app42.paas.sdk.android.user.User;  
import com.shephertz.app42.paas.sdk.android.user.User.Profile;  
import com.shephertz.app42.paas.sdk.android.user.User.UserGender;  
import com.shephertz.app42.paas.sdk.android.user.UserService;  
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.message.Queue;  
import com.shephertz.app42.paas.sdk.android.message.QueueService;  
import com.shephertz.app42.paas.sdk.android.storage.OrderByType;  
import com.shephertz.app42.paas.sdk.android.storage.Query;  
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder;  
import com.shephertz.app42.paas.sdk.android.storage.Storage;  
import com.shephertz.app42.paas.sdk.android.storage.StorageService;  
import com.shephertz.app42.paas.sdk.android.storage.QueryBuilder.Operator;  
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.NotifyListener;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.LobbyData;
import com.shephertz.app42.gaming.multiplayer.client.events.MoveEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;
import java.util.HashMap;

public class App42Singleton extends Godot.SingletonBase implements ConnectionRequestListener, RoomRequestListener, ZoneRequestListener, NotifyListener { 
  private int       instanceId      = 0;
  private Activity m_activity;
  private UserService m_userService;
  private QueueService m_queueService;
  private StorageService m_storageService;
  private WarpClient theClient;
  private RoomData [] roomData;
  private String m_APIKey;
  private String m_secretKey;
  private String m_userName;
  private String m_pwd;
  
  public void setInstanceID(int new_instanceId)
  {
    instanceId = new_instanceId;
  }
  
  public boolean init(String APIKey, String secretKey) {
    // a function to bind
    try
    {
      m_APIKey = APIKey;
      m_secretKey = secretKey;
      
      Context appContext = m_activity.getApplicationContext();
      App42API.initialize(appContext,APIKey,secretKey);
      
      WarpClient.initialize(APIKey, secretKey);
      WarpClient.setRecoveryAllowance(240);
      
      theClient = WarpClient.getInstance();
      theClient.addZoneRequestListener(this);
      theClient.addConnectionRequestListener(this);
      theClient.addRoomRequestListener(this);
      theClient.addNotificationListener(this);
      
      //Build User Service  
      m_userService = App42API.buildUserService();  
      m_queueService = App42API.buildQueueService();  
      m_storageService = App42API.buildStorageService();  
    }catch(Exception e){
      e.printStackTrace();
      return false;
    }
    
    return true;
  }
  
  public void reconnectWarp(){
    log("reconnectWarp");
    theClient.RecoverConnection();
  }
  
  private void log(Object msg){
    System.out.println("godot - app42 - " + msg);
  }
  
  public void createUser(String userName, String pwd, String email) {
    m_userService.createUser( userName, pwd, email, new App42CallBack() 
                               {  
      public void onSuccess(Object response)   
      {  
        /*User user = (User)response;  
         log("userName is " + user.getUserName());  
         log("emailId is " + user.getEmail());  */
        log("_on_createUser_success");
        GodotLib.calldeferred(instanceId, "_on_createUser_success", new Object[]{});
      }  
      
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode()  ;
        log("Exception code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());  
        GodotLib.calldeferred(instanceId, "_on_createUser_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });     
  }
  
  public void authenticate(String userName, String pwd){  
    m_userName= userName;
    m_pwd=pwd;
    m_userService.authenticate(userName , pwd, new App42CallBack() {  
      public void onSuccess(Object response)  
      {  
        User user = (User)response;  
        theClient.connectWithUserName(user.getUserName());
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode()  ;
        log("Exception code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());  
        GodotLib.calldeferred(instanceId, "_on_authenticate_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });  
  }
  public void connectWithUserName(String userName)
  {
    theClient.connectWithUserName(userName);
  }
  
  public void reAuthenticate(){  
    log("reAuthenticate");
    m_userService.authenticate(m_userName , m_pwd, new App42CallBack() {  
      public void onSuccess(Object response)  
      {  
        User user = (User)response;  
        theClient.connectWithUserName(user.getUserName());
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode()  ;
        log("Exception code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());  
        GodotLib.calldeferred(instanceId, "_on_authenticate_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });  
  }
  
  public void getRoomInRange( int minUsers , int maxUsers )   
  {
    log("getRoomInRange");
    theClient.getRoomInRange( minUsers , maxUsers)   ;
  }
  public void joinRoom ( String roomId )
  {
    log("joinRoom: " + roomId);
    theClient.joinRoom(roomId);
  }
  
  @Override
  public void onConnectDone(final ConnectEvent event) {
    log("_on_authenticate_success");
    GodotLib.calldeferred(instanceId, "_on_authenticate_success", new Object[]{});
  }
  
  @Override
  public void onInitUDPDone(byte result) {
    
  }
  @Override
  public void onDisconnectDone(final ConnectEvent event) {
    
  }
  
  public void createQueue(String queueName, String queueDescription){  
    m_queueService.createPullQueue(queueName, queueDescription, new App42CallBack() {  
      public void onSuccess(Object response)   
      {  
        Queue queue  = (Queue)response;  
        log("_on_create_queue_success");
        GodotLib.calldeferred(instanceId, "_on_create_queue_success", new Object[]{queue.getQueueName (), queue.getQueueType (), queue.getDescription() });
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode() ;
        log("_on_create_queue_fail code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());  
        GodotLib.calldeferred(instanceId, "_on_create_queue_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });   
  }
  
  public void createRoom (String name,  String admin, int maxUsers){
    log("CreateRoom");
    theClient.createRoom(name, admin, maxUsers, null);
  }
  
  @Override
  public void onCreateRoomDone(final RoomEvent event) {
    log("onCreateRoomDone");
    if(event.getResult()==WarpResponseResultCode.SUCCESS){// if room created successfully
      String roomId = event.getData().getId();
      log("onCreateRoomDone" + event.getResult()+" "+roomId);
      GodotLib.calldeferred(instanceId, "_on_create_room_success", new Object[]{roomId});
      
    }
    else
    {
      log("onCreateRoomFail result: " + event.getResult());
      GodotLib.calldeferred(instanceId, "_on_create_room_fail", new Object[]{String.valueOf(event.getResult())});
    }
    
  }
  @Override
  public void onRoomDestroyed(RoomData arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onRoomCreated(RoomData arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUnlockPropertiesDone(byte arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onLockPropertiesDone(byte arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onLeaveRoomDone(RoomEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUnSubscribeRoomDone(RoomEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onSubscribeRoomDone(RoomEvent event) {
    if(event.getResult()==0){// Room created successfully
      log("_on_subscribe_room_success");
      String roomId = event.getData().getId();
      GodotLib.calldeferred(instanceId, "_on_subscribe_room_success", new Object[]{roomId});
    }
    else
    {
      log("onSubscribeRoomFail " + String.valueOf(event.getResult()));
      GodotLib.calldeferred(instanceId, "_on_subscribe_room_fail", new Object[]{String.valueOf(event.getResult())});
    }
    
  }
  
  @Override
  public void onJoinRoomDone(final RoomEvent event) {
    if(event.getResult()==0){// Room created successfully
      log("_on_join_room_success");
      String roomId = event.getData().getId();
      GodotLib.calldeferred(instanceId, "_on_join_room_success", new Object[]{roomId});
    }
    else
    {
      log("onJoinRoomFail " + String.valueOf(event.getResult()));
      GodotLib.calldeferred(instanceId, "_on_join_room_fail", new Object[]{String.valueOf(event.getResult())});
    }
    
  }
  
  @Override
  public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onGetOnlineUsersDone(AllUsersEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onGetAllRoomsDone(AllRoomsEvent event) {
    
  }
  @Override
  public void onDeleteRoomDone(RoomEvent event) {
    
  }
  
  
  @Override
  public void onUserLeftLobby(LobbyData arg0, String arg1) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUserLeftRoom(final RoomData roomData, final String userName) {
    log("onJoinRoomFail " + userName);
    GodotLib.calldeferred(instanceId, "_on_user_left_room", new Object[]{userName});
    
  }
  
  @Override
  public void onMoveCompleted(MoveEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onUpdatePeersReceived(UpdateEvent arg0) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onUserPaused(String arg0, boolean arg1, String arg2) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUserResumed(String arg0, boolean arg1, String arg2) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onGameStarted(String arg0, String arg1, String arg2) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onGameStopped(String arg0, String arg1) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUserChangeRoomProperty(RoomData arg0, String arg1,
                                       HashMap<String, Object> arg2, HashMap<String, String> arg3) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onNextTurnRequest(String arg0) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onPrivateUpdateReceived(String arg0, byte[] arg1, boolean arg2) {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void onPrivateChatReceived(final String userName, final String message) {
    
  }
  
  @Override
  public void onChatReceived(final ChatEvent event) {
    log("onChatReceived sender:" + event.getSender());
    GodotLib.calldeferred(instanceId, "_on_chat_received", new Object[]{event.getSender(), event.getMessage()});
  }
  
  @Override
  public void onUserJoinedLobby(LobbyData arg0, String arg1) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public void onUserJoinedRoom(final RoomData roomData, final String userName) {
    log("onUserJoinedRoom username:" + userName);
    GodotLib.calldeferred(instanceId, "_on_user_joined_room", new Object[]{userName});
  }
  
  
  public void receiveMessage(String queueName){  
    long  receiveTimeOut = 10000;
    
    m_queueService.receiveMessage(queueName, receiveTimeOut, new App42CallBack() {  
      public void onSuccess(Object response)   
      {  
        Queue queue  = (Queue)response;  
        ArrayList<Queue.Message> messageList = queue.getMessageList();    
        for(Queue.Message message : messageList)    
        {      
          log("_on_receive_message_success");
          GodotLib.calldeferred(instanceId, "_on_receive_message_success", new Object[]{queue.getQueueName(), queue.getQueueType(), message.getCorrelationId(), message.getMessageId(), message.getPayLoad()});
        }   
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode() ;
        log("_on_receive_message_fail code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());  
        GodotLib.calldeferred(instanceId, "_on_receive_message_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });  
  }
  
  public void sendMessage(String queueName, String message){  
    long  expiryTime = 10000;
    
    m_queueService.sendMessage(queueName, message, expiryTime, new App42CallBack() {  
      public void onSuccess(Object response)  
      {  
        Queue queue  = (Queue)response;   
        ArrayList<Queue.Message> messageList = queue.getMessageList();    
        for(Queue.Message message : messageList)    
        {      
          log("_on_send_message_success");
          GodotLib.calldeferred(instanceId, "_on_send_message_success", new Object[]{queue.getQueueName(), queue.getQueueType(), message.getCorrelationId(), message.getPayLoad()});
        }   
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode() ;
        log("_on_send_message_fail code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());
        GodotLib.calldeferred(instanceId, "_on_send_message_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });  
  }
  
  public void insertJSON(String dbName, String collectionName, String json){  
    m_storageService.insertJSONDocument(dbName, collectionName, json,new App42CallBack() {  
      public void onSuccess(Object response)   
      {  
        /*Storage  storage  = (Storage )response;  
         log("dbName is " + storage.getDbName());  
         log("collection Name is " + storage.getCollectionName());  
         ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();            
         for(int i=0;i<jsonDocList.size();i++)  
         {  
         log("objectId is " + jsonDocList.get(i).getDocId());    
         log("CreatedAt is " + jsonDocList.get(i).getCreatedAt());    
         log("UpdatedAtis " + jsonDocList.get(i).getUpdatedAt());    
         log("Jsondoc is " + jsonDocList.get(i).getJsonDoc());    
         }*/    
        log("_on_insert_json_success");
        GodotLib.calldeferred(instanceId, "_on_insert_json_success", new Object[]{});
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode() ;
        log("_on_insert_json_fail code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());
        GodotLib.calldeferred(instanceId, "_on_insert_json_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });  
  }
  
  public void updateDocumentByDocId(String dbName, String collectionName, String docId, String jsonString){
    
    m_storageService.updateDocumentByDocId(dbName, collectionName, docId, jsonString, new App42CallBack() {  
      public void onSuccess(Object response)   
      {  
        Storage  storage  = (Storage )response;  
        ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();              
        for(int i=0;i<jsonDocList.size();i++)  
        {  
          log("_on_update_doc_by_docid_success"); 
          GodotLib.calldeferred(instanceId, "_on_update_doc_by_docid_success", new Object[]{storage.getDbName(), storage.getCollectionName(), jsonDocList.get(i).getDocId()});
        }   
      }  
      public void onException(Exception ex)   
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode()  ;
        log("_on_update_doc_by_docid_fail code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());   
        GodotLib.calldeferred(instanceId, "_on_update_doc_by_docid_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });   
  }
  public void findDocByKey(String dbName, String collectionName, String key, String value){  
    m_storageService.findDocumentByKeyValue(dbName, collectionName, key, value, new App42CallBack() {  
      public void onSuccess(Object response)   
      {  
        log("_on_find_doc_success");
        Storage  storage  = (Storage )response;  
        ArrayList<Storage.JSONDocument> jsonDocList = storage.getJsonDocList();
        Object []dsf = new Object[jsonDocList.size()];
        String itemRow[] = new String[2];
        int i = 0;
        for(Storage.JSONDocument item: jsonDocList){
          log(item.getJsonDoc());
          itemRow[0]=item.getDocId();
          itemRow[1]=item.getJsonDoc();
          dsf[i]=itemRow;
          i++;
        } 
        log(jsonDocList.size());
        log("_on_find_doc_success - conv");
        GodotLib.calldeferred(instanceId, "_on_find_doc_success", new Object[]{storage.getDbName(), storage.getCollectionName(), dsf});
      }  
      public void onException(Exception ex)  
      {  
        int appErrorCode = ((App42Exception)ex).getAppErrorCode()  ;
        log("_on_find_doc_fail code: " +String.valueOf(appErrorCode) + " Message"+ex.getMessage());   
        GodotLib.calldeferred(instanceId, "_on_find_doc_fail", new Object[]{ex.getMessage(), appErrorCode});
      }  
    });    
  }
  
  @Override
  public void onGetMatchedRoomsDone(final MatchedRoomsEvent event) {
    log("onGetMatchedRoomsDone");
    
    
    RoomData[] roomDataList = event.getRoomsData();
    if (roomDataList==null){
      log("roomList null");
      return;
    }
    int len = roomDataList.length;
    log("len: " + String.valueOf(len));

    Object []outList = new Object[len];
    String itemRow[];
    for(int i =0; i < len; i++)
    {
      log("i: " + String.valueOf(i));
      log(roomDataList[i].getName());
      itemRow = new String[4];
      itemRow[0]=roomDataList[i].getId();
      itemRow[1]=roomDataList[i].getRoomOwner();
      itemRow[2]=roomDataList[i].getName();
      itemRow[3]=String.valueOf(roomDataList[i].getMaxUsers());
      outList[i]=itemRow;
    }
    GodotLib.calldeferred(instanceId, "_on_get_matched_rooms_done", new Object[]{outList});
    
    
  }
  
  
  public void subscribeRoom ( String roomId )   {
    log("subscribeRoom: " + roomId);
    theClient.subscribeRoom(roomId);
  }
  public void sendChat ( String message )   {
    log("sendChat");
    theClient.sendChat(message);
  }
  
  static public Godot.SingletonBase initialize(Activity p_activity) {
    return new App42Singleton(p_activity);
  } 
  
  
  // forwarded callbacks you can reimplement, as SDKs often need them
  
  protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {}
  
  protected void onMainPause() {}
  
  protected void onMainResume() {}
  
  protected void onMainDestroy() {}
  
  protected void onGLDrawFrame(GL10 gl) {}
  
  protected void onGLSurfaceChanged(GL10 gl, int width, int height) {} // singletons will always miss first onGLSurfaceChanged call
  
  public App42Singleton(Activity p_activity) {
    //register class name and functions to bind
    registerClass("App42Singleton", new String[]{"init", "setInstanceID", "createUser", "authenticate", "createQueue", "receiveMessage", "sendMessage"
      , "insertJSON", "findDocByKey", "createRoom", "updateDocumentByDocId", "joinRoom", "reconnectWarp", "subscribeRoom", "sendChat", "reAuthenticate"
      , "connectWithUserName", "getRoomInRange"} );
    m_activity = p_activity;
    
    // you might want to try initializing your singleton here, but android
    // threads are weird and this runs in another thread, so you usually have to do
    p_activity.runOnUiThread(new Runnable() {
      public void run() {
        //useful way to get config info from engine.cfg
        String key = GodotLib.getGlobal("plugin/api_key");
        //SDK.initializeHere();
      }
    });
    
  }
}