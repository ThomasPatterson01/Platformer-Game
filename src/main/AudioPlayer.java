package main;

import org.lwjgl.openal.*;

import menus.SettingsMenu;

import java.nio.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.libc.LibCStdlib.*;

public class AudioPlayer {

	private static long device; 
	private static long context;
	
	private static int musicSourcePointer;
	private static float musicVolume;
	
	public static Map<String, Integer> soundMap = new HashMap<String, Integer>();
	public static Map<String, Integer> musicMap = new HashMap<String, Integer>();
	
	public static void load() {
		//play the audio through the default openAL device
		String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
		device = alcOpenDevice(defaultDeviceName);
		
		//create the context and tell openAL we will be using it
		//empty attributes array because we dont need to specify any
		int[] attributes = {0};
		context = alcCreateContext(device, attributes);
		alcMakeContextCurrent(context);
		
		//needed for OpenAL internal functions, do not remove
		ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
		@SuppressWarnings("unused")
		ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
		
		//create the sound map between sound buffers and sound names
		soundMap.put("playerDeath", loadBuffer("res/audio/deathSound.ogg"));
		soundMap.put("enemyDeath", loadBuffer("res/audio/enemyDeathSound.ogg"));
		soundMap.put("shoot", loadBuffer("res/audio/shootSound.ogg"));
		soundMap.put("groundShake", loadBuffer("res/audio/groundShakeSound.ogg"));
		
		//create the music map between music buffers and music names
		musicMap.put("backgroundMusic", loadBuffer("res/audio/backgroundMusic.ogg"));
	}
	
	//load sound data for a file, and return a pointer to it, to be stored in soundMap
	private static int loadBuffer(String fileName) {
		
		//Allocate space to store return information from the stb_vorbis_decode_filename function
		stackPush();
		IntBuffer channelsBuffer = stackMallocInt(1);
		stackPush();
		IntBuffer sampleRateBuffer = stackMallocInt(1);

		ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(fileName, channelsBuffer, sampleRateBuffer);

		//Retreive the extra information that was stored in the buffers by the stb_vorbis_decode_filename function
		int channels = channelsBuffer.get();
		int sampleRate = sampleRateBuffer.get();
		
		//Free the space allocated above
		stackPop();
		stackPop();
		
		//Find the correct OpenAL format depending on the number of channels
		//if only 1 channel, we only need mono sound, else stereo is used
		int format = -1;
		if(channels == 1) format = AL_FORMAT_MONO16;
		else if(channels == 2) format = AL_FORMAT_STEREO16;

		//Request space for the buffer
		int bufferPointer = alGenBuffers();

		//Send the data to OpenAL
		alBufferData(bufferPointer, format, rawAudioBuffer, sampleRate);

		//Free the memory allocated by STB
		free(rawAudioBuffer);
		
		//return a pointer to the buffered data
		return bufferPointer;
	}
	
	public static void playSound(String soundName, float volume) {
		
		//adjust the volume according to the settings value
		volume *= (float)SettingsMenu.SOUND_VOLUME/100;
		
		int bufferPointer = soundMap.get(soundName);
		
		//Request a source
		int sourcePointer = alGenSources();

		//Assign the sound we just loaded to the source
		alSourcei(sourcePointer, AL_BUFFER, bufferPointer);
		
		//set the volume of the sound
		alSourcef(sourcePointer, AL_GAIN, volume);
		
		//Play the sound
		alSourcePlay(sourcePointer);		
	}
	
	//resets the volume of the background music after it has been changed in settings
	public static void refreshMusicVolume() {
		alSourcef(musicSourcePointer, AL_GAIN, musicVolume * (float)SettingsMenu.MUSIC_VOLUME/100);
	}
	
	public static void playMusic(String soundName, float volume) {
		
		//adjust the volume according to the settings value
		musicVolume = volume;
		volume *= (float)SettingsMenu.MUSIC_VOLUME/100;
		
		int bufferPointer = musicMap.get(soundName);
		
		//Request a source
		musicSourcePointer = alGenSources();

		//Assign the sound we just loaded to the source
		alSourcei(musicSourcePointer, AL_BUFFER, bufferPointer);
		
		alSourcei(musicSourcePointer, AL_LOOPING, AL_TRUE);
		
		//set the volume of the sound
		alSourcef(musicSourcePointer, AL_GAIN, volume);
		
		//Play the sound
		alSourcePlay(musicSourcePointer);
	}
	
	//free resources
	public static void close() {
		alcDestroyContext(context);
		alcCloseDevice(device);
	}
}
