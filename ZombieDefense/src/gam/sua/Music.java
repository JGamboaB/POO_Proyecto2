package gam.sua;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Music {

    //Constructor
    public Music(){}

    /**Plays the background music */
    void playBGMusic(){
        try{
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("music\\Pixel Tunes 8.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**Plays the sound effects
     * 0. Move, 1. Attack, 2. Receive Damage, 3. Enemy Death, 4. Items/Chest, 5. Turn */
    void playSoundEffects(int opt){
        try{
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("music\\"+opt+".wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
