
extends Node2D
onready var app42 = get_node("/root/global").app42

func _ready():
	if app42!= null:
		app42.setInstanceID(get_instance_ID())
	# buttons signals
	get_node("pnlChat/btnSend").connect("pressed",self,"on_btnSend_pressed")
	get_node("pnlChat/btnExit").connect("pressed",self,"on_btnExit_pressed")
	
	
	
	

########################################
## Aux functions
########################################
func add_to_chat(sender, message):
	get_node("pnlChat/rtbChat").set_bbcode(get_node("pnlChat/rtbChat").get_bbcode() + "\r\n" + sender + ": " + message)

########################################
## App42 events
########################################
func _on_chat_received(sender, message):
	add_to_chat(sender, message)
	
########################################
## buttons
########################################
func on_btnExit_pressed():
	get_tree().quit()
	
func on_btnSend_pressed():
	print("on_btnSend_pressed")
	
	var txtSend = get_node("pnlChat/txtSend").get_text()
	if txtSend=="":
		return
		
	app42.sendChat(txtSend)
	get_node("pnlChat/txtSend").set_text("")
	
	