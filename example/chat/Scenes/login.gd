extends Node2D
onready var app42 = get_node("/root/global").app42


func _ready():
	if app42!= null:
		app42.setInstanceID(get_instance_ID())
	get_node("pnlLogin/btnExit").connect("pressed",self, "on_btnExit_pressed")
	get_node("pnlLogin/btnLogin").connect("pressed",self, "on_btnLogin_pressed")
	

########################################
## App42 events
########################################
func _on_authenticate_success():
	print("authenticate_success.")
	get_node("/root/global").goto_scene("res://Scenes/rooms.tscn")
	
	
func _on_authenticate_fail(msg, errCode):
	get_node("/root/global").show_msg("auth error: " + msg)

########################################
## buttons
########################################
func on_btnLogin_pressed():	
	var userName = get_node("pnlLogin/txtUsername").get_text()
	get_node("/root/global").username = userName
	if userName!="":
		get_node("/root/global").wait("Connecting...")
		if app42 != null:
			app42.connectWithUserName(userName)
	else:
		get_node("/root/global").show_msg("Insert a valid username.")


func on_btnExit_pressed():
	get_tree().quit()