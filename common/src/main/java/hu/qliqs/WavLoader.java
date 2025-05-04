package hu.qliqs;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.openal.AL10.*;

public class WavLoader {
    private int source;
    private int buffer;

    public void playWav(InputStream wavInput) {
        try {
            // Load and decode WAV using Java Sound API
            AudioInputStream ais = AudioSystem.getAudioInputStream(wavInput);
            AudioFormat format = ais.getFormat();

            int channels = format.getChannels();
            int sampleRate = (int) format.getSampleRate();
            int alFormat = getOpenALFormat(format);

            // Read PCM data
            byte[] pcmData = ais.readAllBytes();
            ByteBuffer data = ByteBuffer.allocateDirect(pcmData.length).order(ByteOrder.nativeOrder());
            data.put(pcmData);
            data.flip();

            // Create OpenAL buffer and source
            buffer = alGenBuffers();
            alBufferData(buffer, alFormat, data, sampleRate);

            source = alGenSources();
            alSourcei(source, AL_BUFFER, buffer);
            alSourcePlay(source);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getOpenALFormat(AudioFormat format) {
        if (format.getChannels() == 1) {
            return format.getSampleSizeInBits() == 8 ? AL_FORMAT_MONO8 : AL_FORMAT_MONO16;
        } else if (format.getChannels() == 2) {
            return format.getSampleSizeInBits() == 8 ? AL_FORMAT_STEREO8 : AL_FORMAT_STEREO16;
        }
        throw new IllegalArgumentException("Unsupported WAV format");
    }

    public void cleanup() {
        alDeleteSources(source);
        alDeleteBuffers(buffer);
    }
}
