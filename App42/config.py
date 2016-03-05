def can_build(plat):
	return plat=="android"

def configure(env):
	if (env['platform'] == 'android'):
		env.android_add_dependency("compile files('../../../modules/App42/android/App42MultiPlayerGamingSDK.jar')")
		env.android_add_dependency("compile files('../../../modules/App42/android/App42_ANDROID_SDK_3.6.jar')")		
		env.android_add_java_dir("android")
		#env.disable_module()
