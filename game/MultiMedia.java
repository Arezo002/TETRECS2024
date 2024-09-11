package uk.ac.soton.comp1206.game;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Multimedia class manages the sound and images for the game
 */
public class MultiMedia {
    private static final Logger logger = LogManager.getLogger(MultiMedia.class);
    /**
    *audio variables
     */
    private static MediaPlayer audioPlayer;
    private static MediaPlayer bMusicPlayer;

    /** Property to track if audio is enabled or disabled. */
    private static SimpleBooleanProperty audioEnabled = new SimpleBooleanProperty(true);

    /**
     * empty constructor
     */
    public MultiMedia(){
    }

    /**
     * method to play short audios
     * @param audio - string audio file name
     */
    public static void playAudio(String audio) {
        var audioFile = MultiMedia.class.getResource("/sounds/" + audio).toExternalForm();
        audioPlayer = new MediaPlayer(new Media(audioFile));
        audioPlayer.play();
        logger.info("Audio played: " + audio);
    }


    /**
     * method to play background music - loops the music
     * @param musicFile - takes a music file
     */
    public static void playBackgroundMusic(String musicFile){
        //stops background music so when one background music File starts another stops
        stopBackgroundMusic();
        if(!audioEnabled.get()){
            logger.info("Audio is disabled, not playing background music: " + musicFile);
            return;
        }

        try{
            Media bMusic = new Media(MultiMedia.class.getResource("/music/" + musicFile).toExternalForm());
            bMusicPlayer = new MediaPlayer(bMusic);
            bMusicPlayer.setVolume(0.2);
            bMusicPlayer.play();
            bMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            logger.info("playing background music: " + musicFile);
        }catch (Exception e){
            e.printStackTrace();
            audioEnabled.set(false);
            logger.error("unable to play sound: " + musicFile);
            logger.error("Disabling sound");
        }
    }

    /**
     * stops background music
     */
    public static void stopBackgroundMusic() {
        if (bMusicPlayer != null) {
            bMusicPlayer.stop();
            bMusicPlayer = null;
        }
    }
}
