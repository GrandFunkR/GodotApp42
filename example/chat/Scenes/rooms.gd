extends Node2D
onready var app42 = get_node("/root/global").app42


func _ready():
	if app42!= null:
		app42.setInstanceID(get_instance_ID())
		app42.getRoomInRange(0,10)
	get_node("pnlRoom/btnExit").connect("pressed",self, "on_btnExit_pressed")
	get_node("pnlRoom/btnEnter").connect("pressed",self, "on_btnEnter_pressed")
	get_node("pnlRoom/roomList").connect("item_selected",self,"on_item_selected")
	

func on_item_selected(index):
	get_node("pnlRoom/txtRoom").set_text(get_node("pnlRoom/roomList").get_item_text(index))
	
########################################
## App42 events
########################################
func _on_get_matched_rooms_done(roomsList):
	var list = get_node("pnlRoom/roomList")
	print("_on_get_matched_rooms_done")
	print(roomsList)
	list.clear()
	for item in roomsList:
		list.add_item(item[2], null)
		list.set_item_metadata(list.get_item_count()-1, item[0])
	
func _on_join_room_success(roomId):
	print("_on_join_room_success")
	app42.subscribeRoom(roomId)
	
func _on_join_room_fail(result):
	print("_on_join_room_fail " + str(result))
	get_node("/root/global").show_msg("_on_join_room_fail result:" + str(result))

func _on_subscribe_room_success(roomId):
	print ("_on_subscribe_room_success: " + roomId)
	get_node("/root/global").goto_scene("res://Scenes/chat.tscn")

func _on_subscribe_room_fail(result):
	get_node("/root/global").show_msg("_on_subscribe_room_fail result:" + str(result))
	
func _on_create_room_success(roomId):
	print("_on_create_room_success")
	app42.joinRoom(roomId)

func _on_create_room_fail(result):
	print("_on_create_room_fail " + str(result))
	get_node("/root/global").show_msg("_on_create_room_fail result:" + str(result))
		
########################################
## buttons
########################################
func on_btnEnter_pressed():	
	var room = get_node("pnlRoom/txtRoom").get_text()
	
	if room!="":
		get_node("/root/global").wait("connecting ro room.")
		var list = get_node("pnlRoom/roomList")
		var i
		for i in range(list.get_item_count()):
			if list.get_item_text(i)==room:
				app42.joinRoom(list.get_item_metadata(i))
				return
		
		app42.createRoom (get_node("pnlRoom/txtRoom").get_text(),  get_node("/root/global").username, 10)
	else:
		get_node("/root/global").show_msg("Insert a valid room name.")


func on_btnExit_pressed():
	get_tree().quit()

