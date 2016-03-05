# App42 warp godot android module

This is an App42 warp (http://appwarp.shephertz.com/) module for Godot Engine (https://github.com/okamstudio/godot)


  - Only for Android

### How to use
1. Drop the "App42" directory inside the "modules" directory on the Godot source.
2. Recompile
3. In your project: 
* edit file engine.cfg add
```
[android]
modules="com/android/godot/GodotGPS"
```
* Export->Target->Android
```
Options:
    Permissions on:
        - Access Network State
        - Access Wifi State
        - Internet
```
* Init App42 Singleton and init app42 with yours App42 API key amd Secret key
* To refer to the callback functions use setInstanceID and then just write the callback function as in example below:
```
var app42

func _ready():
    if(Globals.has_singleton("App42Singleton")):
		app42		= Globals.get_singleton("App42Singleton")
		app42.setInstanceID(get_instance_ID())
		if !(app42.init("Your API Key", "Your Secret Key")):
			print("Not connected to server.")

func _on_authenticate_success():
	print("authenticated")

func _on_authenticate_fail(msg, errCode):
	print("not authenticated")
```
### Methods
- **bool init(String APIKey, String secretKey)**
    > ret vals:
    > true -> success
    > false -> failure

- **void createUser(String userName, String pwd, String email)**
- **void authenticate(String userName, String pwd)**
- **void createQueue(String queueName, String queueDescription)**
- **void receiveMessage(String queueName)**
- **void sendMessage(String queueName, String message)**
- **void insertJSON(String dbName, String collectionName, String json)**
- **void findDocByKey(String dbName, String collectionName, String key, String value)**
- **void createRoom (String name,  String admin, int maxUsers)**
- **void joinRoom ( String roomId )**
- **void updateDocumentByDocId(String dbName, String collectionName, String docId, String jsonString)**
- **void reconnectWarp()**
- **void subscribeRoom ( String roomId )**
- **void sendChat ( String message )**
- **void reAuthenticate()**
- **void connectWithUserName(String username)**
- **void getRoomInRange( int minUsers , int maxUsers )**

### Events
- **_on_createUser_success()**
- **_on_createUser_fail(errorMessage, errorCode)**
- **_on_authenticate_success()**
- **_on_authenticate_fail(errorMessage, errorCode)**
- **_on_create_queue_success(queueName, queueType, queueDescription)**
- **_on_create_queue_fail(errorMessage, errorCode)**
- **_on_receive_message_success(queueName,queueType,messCorrelationId,messId,messPayLoad)**
- **_on_receive_fail(errorMessage, errorCode)**
- **_on_send_message_success(queueName,queueType,messCorrelationId,messPayLoad)**
- **_on_send_message_fail(errorMessage, errorCode)**
- **_on_insert_json_fail(errorMessage, errorCode)**
- **_on_insert_json_success()**
- **_on_join_room_success(roomId)**
- **_on_join_room_fail(result)**
- **_on_create_room_success(roomId)**
- **_on_create_room_fail(result)**
- **_on_find_doc_success(DbName, collectionName, jsonDocList)**
	- jsonDocList is an Array of Array.
        >   For each Row:
        >   0->DocId
        >	1->JSONDoc content
- **_on_find_doc_fail(message, appErrorCode)**
- **_on_update_doc_by_docid_fail(message, appErrorCode)**
- **_on_update_doc_by_docid_success(dbName,collectionName, docId)**
- **_on_subscribe_room_success()**
- **_on_subscribe_room_fail(result)**
- **_on_user_left_room(username)**
- **_on_chat_received(sender, message)**
- **_on_user_joined_room(userName)**
- **_on_get_matched_rooms_done(roomsList)**
    - roomsList is an Array of Array.
        >   For each Row:
        >   0->RoomId
        >   1->RoomOwner
        >   2->RoomName
        >   3->MaxUsers


