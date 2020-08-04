package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Initializes all view objects variables from the expanded_details.xml file
    ImageButton bLike, bNotLike, dislike, notDisLike;
    ImageButton bPlay, bPause, play_main, pause_main;
    private SlidingUpPanelLayout mLayout;
    private SeekBar mSeekBar;

    Handler handler;
    Runnable runnable;

    //Handles playback of all the sound files
    private MediaPlayer mMediaPlayer;


    //Handles audio focus when playing a sound file
    private AudioManager mAudioManager;


    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };


    //This listener gets triggered when the MediaPlayer has completed playing the audio file.
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //enables ToolBar in the main activity layout
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //defines all the variables from the class
        bLike = findViewById(R.id.imageButton2);
        bNotLike = findViewById(R.id.imageButton2new);
        dislike = findViewById(R.id.button);
        notDisLike = findViewById(R.id.buttontwo);
        bPlay = findViewById(R.id.play_button);
        bPause = findViewById(R.id.pause_button);
        play_main = findViewById(R.id.play_button_main);
        pause_main = findViewById(R.id.pause_button_main);
        mSeekBar = findViewById(R.id.seekBar);
        handler = new Handler();

        mLayout = findViewById(R.id.activity_main);

        // Create and setup the AudioManager to request audio focus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //Creates an adapter array of Song class type
        final ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song(getString(R.string.por_un_beso_song), getString(R.string.aventura_artist), R.drawable.aventura, R.raw.aventura_por_un_beso));
        songs.add(new Song(getString(R.string.dime_si_te_gusto_song), getString(R.string.aventura_artist), R.drawable.aventura, R.raw.aventura_dime_si_te_gusto));
        songs.add(new Song(getString(R.string.alexandra_song), getString(R.string.aventura_artist), R.drawable.aventura, R.raw.aventura_alexandra));
        songs.add(new Song(getString(R.string.la_novelita_song), getString(R.string.aventura_artist), R.drawable.aventura, R.raw.aventura_novelita));
        songs.add(new Song(getString(R.string.vivir_mi_vida_song), getString(R.string.marc_anthony_artist), R.drawable.marc_anthony_vivir, R.raw.marc_antony_vivir));
        songs.add(new Song(getString(R.string.propuesta_indecente_song), getString(R.string.romeo_santos_artist), R.drawable.romeo_santos_formula, R.raw.romeo_santos_propuesta));

        SongAdapter adapter = new SongAdapter(this, songs);

        ListView listView = findViewById(R.id.list);

        listView.setAdapter(adapter);

        // Set a click listener to play the audio when the list item is clicked on
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Release the media player if it currently exists because we are about to
                // play a different sound file
                releaseMediaPlayer();

                // Get the Song class object at the given position the user clicked on
                Song song = songs.get(position);


                ImageView currentSongImage = findViewById(R.id.songsCoverOne);
                currentSongImage.setImageResource(song.getImageId());

                ImageView currentSongPrimaryImage = findViewById(R.id.primaryImage);
                currentSongPrimaryImage.setImageResource(song.getImageId());

                TextView currentSongTitle = findViewById(R.id.songsTitle);
                currentSongTitle.setText(song.getSongTitle());

                TextView currentSongArtist = findViewById(R.id.songsArtistName);
                currentSongArtist.setText(song.getArtistName());


                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // We have audio focus now.

                    // Create and setup the MediaPlayer for the audio resource associated
                    // with the current word
                    mMediaPlayer = MediaPlayer.create(MainActivity.this, song.getAudioResourceId());
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mSeekBar.setMax(mMediaPlayer.getDuration());
                            playCycle();
                            play_main.setVisibility(View.GONE);
                            pause_main.setVisibility(View.VISIBLE);
                            bPlay.setVisibility(View.GONE);
                            bPause.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Song Is now Playing", Toast.LENGTH_SHORT).show();
                            // Start the audio file
                            mMediaPlayer.start();

                        }

                    });

                    play_main.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            play();
                        }
                    });

                    bPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            play();
                        }
                    });

                    pause_main.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pause();
                        }
                    });

                    bPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pause();
                        }
                    });


                    // Setup a listener on the media player, so that we can stop and release the
                    // media player once the sound has finished playing.
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            bPause.setVisibility(View.GONE);
                            bPlay.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Song has finished", Toast.LENGTH_SHORT).show();
                            if (pause_main.getVisibility() == View.VISIBLE) {
                                pause_main.setVisibility(View.GONE);
                                play_main.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mMediaPlayer != null) {
                                try {
                                    if (mMediaPlayer.isPlaying()) {
                                        Message message = new Message();
                                        message.what = mMediaPlayer.getCurrentPosition();
                                        handler.sendMessage(message);
                                        Thread.sleep(1000);
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();

                    //setting up the seek bar
                    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        //do something when user toggles through the seek bar
                        @Override
                        public void onProgressChanged(SeekBar sBar, int i, boolean b) {
                            if (b) {
                                mMediaPlayer.seekTo(i); // seek to the current time of song
                                mSeekBar.setProgress(i); // set progress of seek bar
                            }

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    //update sound when user toggles through seek bar
                    mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                            double ratio = i / 100.0;
                            int bufferingLevel = (int) (mediaPlayer.getDuration() * ratio);
                            mSeekBar.setSecondaryProgress(bufferingLevel);
                        }
                    });
                }
            }
        });


        bLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bNotLike.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "You liked the song", Toast.LENGTH_SHORT).show();
                if (notDisLike.getVisibility() == View.VISIBLE) {
                    notDisLike.setVisibility(View.GONE);
                }
            }
        });

        bNotLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bNotLike.setVisibility(View.GONE);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notDisLike.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "You disliked the song", Toast.LENGTH_SHORT).show();
                if (bNotLike.getVisibility() == View.VISIBLE) {
                    bNotLike.setVisibility(View.GONE);
                }
            }
        });

        notDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notDisLike.setVisibility(View.GONE);
            }
        });


    }

    public void playCycle() {
        mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
        if (mMediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();

                }
            };
            handler.postDelayed(runnable, 1000);

        }
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null && (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // When the activity is stopped, release the media player resources because we won't
        // be playing any more sounds.
        releaseMediaPlayer();
    }


    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {

            mMediaPlayer.release();


            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    //method called when either of the two play buttons are pressed
    private void play() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            pause_main.setVisibility(View.GONE);
            play_main.setVisibility(View.VISIBLE);
            bPause.setVisibility(View.GONE);
            bPlay.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Song is paused", Toast.LENGTH_SHORT).show();

        } else {
            mMediaPlayer.start();
            play_main.setVisibility(View.GONE);
            pause_main.setVisibility(View.VISIBLE);
            bPlay.setVisibility(View.GONE);
            bPause.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Song is now playing", Toast.LENGTH_SHORT).show();

        }
    }

    //method called when either of the two pause buttons are pressed
    private void pause() {
        mMediaPlayer.pause();
        pause_main.setVisibility(View.GONE);
        play_main.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "Song is paused", Toast.LENGTH_SHORT).show();
        if (bPause.getVisibility() == View.VISIBLE) {
            bPause.setVisibility(View.GONE);
            bPlay.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //by clicking the action bar button, this will navigate to the AddMusicActivity
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, AddMusicActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}