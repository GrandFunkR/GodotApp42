
extends Patch9Frame


func _on_btnOk_pressed():
	get_tree().set_pause(false)
	get_parent().get_child(0).set_opacity(1)
	self.hide()
