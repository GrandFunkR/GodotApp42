extends Node
var current_scene = null
var app42 #app42 singleton
var username

func _ready():
	var root = get_tree().get_root()
	current_scene = root.get_child(root.get_child_count() -1)
	
	if(Globals.has_singleton("App42Singleton")):
		app42		= Globals.get_singleton("App42Singleton")
		app42.setInstanceID(get_instance_ID())
		if !(app42.init("Your API Key", "Your Secret Key")):
			show_msg("Not connected to server.\r\nPlease exit and try again.")
	if app42 == null:
		show_msg("Not connected to server.\r\nAre you sure you are on an android device?\r\nplease exit and try again.")

func show_msg(msg):
	stop_wait()
	var scnMsg = ResourceLoader.load("res://Scenes/msg.tscn").instance()
	current_scene.get_child(0).set_opacity(0.25)
	scnMsg.get_node("lblMsg").set_bbcode(msg)
	current_scene.add_child(scnMsg)
	get_tree().set_pause(true)

	
func wait(msg):
	get_tree().set_pause(true)
	current_scene.get_child(0).set_opacity(0.25)
	
	if current_scene.has_node("pnlWait"):
		current_scene.get_node("pnlWait").show()
	else:
		var scnWait = ResourceLoader.load("res://Scenes/wait.tscn").instance()
		scnWait.get_node("lblMsg").set_text(msg)
		current_scene.add_child(scnWait)
	
func stop_wait():
	get_tree().set_pause(false)
	current_scene.get_child(0).set_opacity(1)
	if current_scene.has_node("pnlWait"):
		current_scene.get_node("pnlWait").hide()

func goto_scene(path):
	stop_wait()
	current_scene.queue_free()
	
	# Load new scene
	var s = ResourceLoader.load(path)
	
	# Instance the new scene
	current_scene = s.instance()
	
	# Add it to the active scene, as child of root
	get_tree().get_root().add_child(current_scene)
	
	#optional, to make it compatible with the SceneTree.change_scene() API
	get_tree().set_current_scene( current_scene )
	