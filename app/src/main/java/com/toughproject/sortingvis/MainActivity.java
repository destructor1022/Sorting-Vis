package com.toughproject.sortingvis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends WearableActivity {

    private static int hi;

    public static class main_screen_traits {
        private static Random random = new Random();
        private static int animation_time = 300;
        private static int start_animation_time = 500;
        private static int fg_circle_count = 11;
        private static int bg_circle_count = 11;
        private static int placer_circle_count = 4;
        private static int interaction_button_count = 3;
        private static int number_of_sorts = sorting_algos.sort_name.length;
        private static int type_sort = random.nextInt(number_of_sorts);


        public static int getFg_circle_count() {
            return fg_circle_count;
        }

        public static void setFg_circle_count(int fg_circle_count) {
            main_screen_traits.fg_circle_count = fg_circle_count;
        }

        public static int getBg_circle_count() {
            return bg_circle_count;
        }

        public static void setBg_circle_count(int bg_circle_count) {
            main_screen_traits.bg_circle_count = bg_circle_count;
        }

        public static int getPlacer_circle_count() {
            return placer_circle_count;
        }

        public static int getAnimation_time() {
            return animation_time;
        }

        public static void setAnimation_time(int animation_time) {
            main_screen_traits.animation_time = animation_time;
        }

        public static int getStart_animation_time() {
            return start_animation_time;
        }

        public static int getInteraction_button_count() {
            return interaction_button_count;
        }

        public static int getType_sort() {
            return type_sort;
        }

        public static void setType_sort(int type_sort) {
            int true_type_sort;
            if(type_sort < 0) {
                true_type_sort = number_of_sorts - 1;
            } else if(type_sort > number_of_sorts - 1) {
                true_type_sort = 0;
            } else {
                true_type_sort = type_sort;
            }
            main_screen_traits.type_sort = true_type_sort;
        }

        public static class sorting_algos {
            private static String[] sort_name = {"BubbleSort", "BogoSort", "QuickSort"};
            private static int[][] color_pallet = {
                    {0, 255, 0},
                    {255, 0, 0},
                    {255, 255, 0}
                    };

            public static String getSort_name() {
                return(sort_name[getType_sort()]);
            }

            public static int getRed() {
                return(color_pallet[getType_sort()][0]);
            }

            public static int getGreen() {
                return(color_pallet[getType_sort()][1]);
            }

            public static int getBlue() {
                return(color_pallet[getType_sort()][2]);
            }




        }

    }

    public class circle {
        private ImageView image_view;
        private int alpha;

        public ImageView getImage_view() {
            return image_view;
        }
        public int getAlpha() {
            return alpha;
        }
        public float getX() {
            return image_view.getX();
        }
        public float getY() {
            return image_view.getY();
        }

        public void setImage_view(ImageView image_view) {
            this.image_view = image_view;
        }
        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

    }


    public static SharedPreferences getSharedPreferences (Context ctxt) {
        return ctxt.getSharedPreferences("preferences", 0);
    }

    final static circle[] fg_circle = new circle[main_screen_traits.getFg_circle_count()];

    final static circle[] bg_circle = new circle[main_screen_traits.getBg_circle_count()];

    final static circle[] placer_circle = new circle[main_screen_traits.getPlacer_circle_count()];

    final static Button[] interaction_button = new Button[main_screen_traits.getInteraction_button_count()];

    static TextClock quick_time;

    static TextView sort_type;

    static TextView stopwatch;
    static TextView stopwatch_text;

    static TextView comp_counter;
    static TextView comp_counter_text;

    static ImageView settings_button;

    static end_class end_sort;

    long start_time = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timer_handler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - start_time;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            stopwatch.setText(String.format("%d:%02d", minutes, seconds));

            timer_handler.postDelayed(this, 500);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RelativeLayout root_layout = (RelativeLayout) findViewById(R.id.root_layout);




        root_layout.post( new Runnable() {
            @Override
            public void run() {
                //run everything in here after layout has been updated
                end_sort = new end_class();
                Random random = new Random();


                //create the middle background circle
                for(int i = main_screen_traits.getBg_circle_count() / 2; i == main_screen_traits.getBg_circle_count() / 2; i++) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 255, 0, 0);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    bg_circle[i] = new circle();

                    bg_circle[i].setAlpha(color);
                    bg_circle[i].setImage_view(circles);
                }

                //create all the background circles to the left of the middle background circle
                for(int i = main_screen_traits.getBg_circle_count() / 2 - 1; i >= 0; i--) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.addRule(RelativeLayout.LEFT_OF, bg_circle[i + 1].getImage_view().getId());
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 255, 0, 0);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    bg_circle[i] = new circle();

                    bg_circle[i].setAlpha(color);
                    bg_circle[i].setImage_view(circles);
                }

                //create all the background circles to the right of the middle background circle
                for(int i = main_screen_traits.getBg_circle_count() / 2 + 1; i < main_screen_traits.getBg_circle_count(); i++) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_VERTICAL);
                    lp.addRule(RelativeLayout.RIGHT_OF, bg_circle[i - 1].getImage_view().getId());
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 255, 0, 0);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    bg_circle[i] = new circle();

                    bg_circle[i].setAlpha(color);
                    bg_circle[i].setImage_view(circles);
                }

                //create the foreground circles in the center of the layout
                for(int i = 0; i < main_screen_traits.getFg_circle_count(); i++) {

                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.CENTER_IN_PARENT);
                    root_layout.addView(circles, lp);
                    int alpha = random.nextInt(256 - 50) + 50;
                    int color = android.graphics.Color.argb(alpha, main_screen_traits.sorting_algos.getRed(), main_screen_traits.sorting_algos.getGreen(), main_screen_traits.sorting_algos.getBlue());
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    fg_circle[i] = new circle();

                    fg_circle[i].setAlpha(alpha);
                    fg_circle[i].setImage_view(circles);

                }

                //create the top center placer circle
                for(int i = 0; i == 0; i++) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 0, 0, 255);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    placer_circle[i] = new circle();

                    placer_circle[i].setAlpha(color);
                    placer_circle[i].setImage_view(circles);
                }

                //create the left placer circle
                for(int i = 1; i == 1; i++) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.BELOW, placer_circle[0].getImage_view().getId());
                    lp.addRule(RelativeLayout.LEFT_OF, placer_circle[0].getImage_view().getId());
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 0, 0, 255);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    placer_circle[i] = new circle();

                    placer_circle[i].setAlpha(color);
                    placer_circle[i].setImage_view(circles);
                }

                //create the right placer circle
                for(int i = 2; i == 2; i++) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.BELOW, placer_circle[0].getImage_view().getId());
                    lp.addRule(RelativeLayout.RIGHT_OF, placer_circle[0].getImage_view().getId());
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 0, 0, 255);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    placer_circle[i] = new circle();

                    placer_circle[i].setAlpha(color);
                    placer_circle[i].setImage_view(circles);
                }

                //create the bottom placer circle
                for(int i = 3; i == 3; i++) {
                    ImageView circles = new ImageView(getApplicationContext());
                    circles.setId(View.generateViewId());
                    circles.setImageDrawable(getDrawable(R.drawable.circle));


                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.BELOW, placer_circle[1].getImage_view().getId());
                    lp.addRule(RelativeLayout.RIGHT_OF, placer_circle[1].getImage_view().getId());
                    root_layout.addView(circles, lp);

                    int color = android.graphics.Color.argb(0, 0, 0, 255);
                    circles.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    placer_circle[i] = new circle();

                    placer_circle[i].setAlpha(color);
                    placer_circle[i].setImage_view(circles);
                }

                //create the three interaction buttons: start, left, and right
                for(int i = 0; i < 3; i++) {
                    if(i == 0) {
                        interaction_button[i] = new Button(getApplicationContext());
                        interaction_button[i].setId(View.generateViewId());
                        interaction_button[i].setText("Start");
                        interaction_button[i].setVisibility(View.INVISIBLE);
                        interaction_button[i].setAlpha(0);

                        LayoutParams lp = new LayoutParams((int) (74 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f), LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, placer_circle[1].getImage_view().getId());
                        lp.addRule(RelativeLayout.LEFT_OF, placer_circle[0].getImage_view().getId());
                        root_layout.addView(interaction_button[i], lp);
                    } else if(i == 1) {
                        interaction_button[i] = new Button(getApplicationContext());
                        interaction_button[i].setId(View.generateViewId());
                        interaction_button[i].setText("❰");
                        interaction_button[i].setVisibility(View.INVISIBLE);
                        interaction_button[i].setAlpha(0);

                        LayoutParams lp = new LayoutParams((int) (37 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f), LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, placer_circle[1].getImage_view().getId());
                        lp.addRule(RelativeLayout.RIGHT_OF, placer_circle[0].getImage_view().getId());
                        root_layout.addView(interaction_button[i], lp);
                    } else if(i == 2) {
                        interaction_button[i] = new Button(getApplicationContext());
                        interaction_button[i].setId(View.generateViewId());
                        interaction_button[i].setText("❱");
                        interaction_button[i].setVisibility(View.INVISIBLE);
                        interaction_button[i].setAlpha(0);

                        LayoutParams lp = new LayoutParams((int) (37 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f), LayoutParams.WRAP_CONTENT);
                        lp.addRule(RelativeLayout.BELOW, placer_circle[1].getImage_view().getId());
                        lp.addRule(RelativeLayout.RIGHT_OF, interaction_button[1].getId());
                        root_layout.addView(interaction_button[i], lp);
                    }
                }

                //create the textclock quicktime so the user is able to still see the time while the app is running
                for(int i = 0; i == 0; i++) {
                    quick_time = new TextClock(getApplicationContext());
                    quick_time.setId(View.generateViewId());
                    quick_time.setTextColor(android.graphics.Color.rgb(100, 100, 100));
                    quick_time.setVisibility(View.INVISIBLE);
                    quick_time.setAlpha(0);
                    quick_time.setTextSize(12);

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    root_layout.addView(quick_time, lp);
                }

                //this is the type of sort that will occur
                for(int i = 0; i == 0; i++) {
                    sort_type = new TextView(getApplicationContext());
                    sort_type.setId(View.generateViewId());
                    sort_type.setTextColor(android.graphics.Color.rgb(170, 170, 170));
                    sort_type.setVisibility(View.INVISIBLE);
                    sort_type.setAlpha(0);
                    sort_type.setTextSize(14);
                    sort_type.setText(main_screen_traits.sorting_algos.getSort_name());

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ABOVE, quick_time.getId());
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp.setMargins(0, 0, 0, (int) (3 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f));
                    root_layout.addView(sort_type, lp);
                }

                //this is the stopwatch
                for(int i = 0; i == 0; i++) {
                    stopwatch = new TextView(getApplicationContext());
                    stopwatch.setId(View.generateViewId());
                    stopwatch.setTextColor(android.graphics.Color.rgb(170, 170, 170));
                    stopwatch.setVisibility(View.INVISIBLE);
                    stopwatch.setAlpha(0);
                    stopwatch.setTextSize(14);
                    stopwatch.setText("0:00");

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ABOVE, sort_type.getId());
                    lp.addRule(RelativeLayout.RIGHT_OF, placer_circle[0].getImage_view().getId());
                    lp.setMargins(0, 0, 0, (int) (3 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f));
                    root_layout.addView(stopwatch, lp);
                }

                //this is the time textview
                for(int i = 0; i == 0; i++) {
                    stopwatch_text = new TextView(getApplicationContext());
                    stopwatch_text.setId(View.generateViewId());
                    stopwatch_text.setTextColor(android.graphics.Color.rgb(170, 170, 170));
                    stopwatch_text.setVisibility(View.INVISIBLE);
                    stopwatch_text.setAlpha(0);
                    stopwatch_text.setTextSize(14);
                    stopwatch_text.setText("Time (min:sec)");

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ABOVE, stopwatch.getId());
                    lp.addRule(RelativeLayout.RIGHT_OF, placer_circle[0].getImage_view().getId());
                    root_layout.addView(stopwatch_text, lp);
                }

                //this is the number of comparisons
                for(int i = 0; i == 0; i++) {
                    comp_counter = new TextView(getApplicationContext());
                    comp_counter.setId(View.generateViewId());
                    comp_counter.setTextColor(android.graphics.Color.rgb(170, 170, 170));
                    comp_counter.setVisibility(View.INVISIBLE);
                    comp_counter.setAlpha(0);
                    comp_counter.setTextSize(14);
                    comp_counter.setText("0");

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ABOVE, sort_type.getId());
                    lp.addRule(RelativeLayout.LEFT_OF, placer_circle[0].getImage_view().getId());
                    lp.setMargins(0, 0, 0, (int) (3 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f));
                    root_layout.addView(comp_counter, lp);
                }

                //this is the comparisons textview
                for(int i = 0; i == 0; i++) {
                    comp_counter_text = new TextView(getApplicationContext());
                    comp_counter_text.setId(View.generateViewId());
                    comp_counter_text.setTextColor(android.graphics.Color.rgb(170, 170, 170));
                    comp_counter_text.setVisibility(View.INVISIBLE);
                    comp_counter_text.setAlpha(0);
                    comp_counter_text.setTextSize(14);
                    comp_counter_text.setText("Comparisons");

                    LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ABOVE, comp_counter.getId());
                    lp.addRule(RelativeLayout.LEFT_OF, placer_circle[0].getImage_view().getId());
                    root_layout.addView(comp_counter_text, lp);
                }

                //this is the imagebutton for settings
                for(int i = 0; i == 0; i++) {
                    settings_button = new ImageView(getApplicationContext());
                    settings_button.setId(View.generateViewId());
                    settings_button.setImageDrawable(getDrawable(R.drawable.settings_image));
                    settings_button.setVisibility(View.INVISIBLE);
                    settings_button.setAlpha(0.0f);
                    int color = android.graphics.Color.argb(255, 170, 170, 170);
                    settings_button.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    LayoutParams lp = new LayoutParams((int) (15 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f), (int) (15 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f));
                    lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp.setMargins(0, (int) (6 * getApplicationContext().getResources().getDisplayMetrics().density + 0.5f), 0, 0);
                    root_layout.addView(settings_button, lp);

                }


            }
        });



        root_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //animate the left foreground circles to jump to position
                for(int i = 0; i < main_screen_traits.getFg_circle_count() / 2; i++) {
                    Path path1 = new Path();
                    path1.moveTo(fg_circle[i].getX(), fg_circle[i].getY());
                    path1.cubicTo(placer_circle[0].getX(), placer_circle[1].getY(), placer_circle[0].getX(), placer_circle[1].getY(), bg_circle[i].getX(), bg_circle[i].getY());

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[i].getImage_view(), View.X, View.Y, path1);
                    animation1.setDuration(main_screen_traits.getStart_animation_time());


                    animation1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                    });

                    animation1.start();
                }

                //animate the right foreground circles to jump to position
                for(int i = main_screen_traits.getFg_circle_count() - 1; i > main_screen_traits.getFg_circle_count() / 2; i--) {
                    Path path1 = new Path();
                    path1.moveTo(fg_circle[i].getX(), fg_circle[i].getY());
                    path1.cubicTo(placer_circle[0].getX(), placer_circle[1].getY(), placer_circle[0].getX(), placer_circle[1].getY(), bg_circle[i].getX(), bg_circle[i].getY());

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[i].getImage_view(), View.X, View.Y, path1);
                    animation1.setDuration(main_screen_traits.getStart_animation_time());


                    animation1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                    });

                    animation1.start();
                }

                //animate the center foreground circle to jump to position
                for(int i = main_screen_traits.getFg_circle_count() / 2; i == main_screen_traits.getFg_circle_count() / 2; i++) {
                    Path path1 = new Path();
                    path1.moveTo(fg_circle[i].getX(), fg_circle[i].getY());
                    path1.cubicTo(placer_circle[0].getX(), placer_circle[1].getY(), placer_circle[0].getX(), placer_circle[1].getY(), bg_circle[i].getX(), bg_circle[i].getY());

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[i].getImage_view(), View.X, View.Y, path1);
                    animation1.setDuration(main_screen_traits.getStart_animation_time());


                    animation1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //animate the buttons and textviews to position via fade in
                            for(int i = 0; i < 3; i++) {
                                interaction_button[i].setVisibility(View.VISIBLE);
                                interaction_button[i].animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();
                            }

                            quick_time.setVisibility(View.VISIBLE);
                            quick_time.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();

                            sort_type.setVisibility(View.VISIBLE);
                            sort_type.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();

                            stopwatch.setVisibility(View.VISIBLE);
                            stopwatch.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();

                            stopwatch_text.setVisibility(View.VISIBLE);
                            stopwatch_text.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();

                            comp_counter.setVisibility(View.VISIBLE);
                            comp_counter.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();

                            comp_counter_text.setVisibility(View.VISIBLE);
                            comp_counter_text.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();

                            settings_button.setVisibility(View.VISIBLE);
                            settings_button.animate().alpha(1.0f).setDuration(main_screen_traits.getStart_animation_time()).start();
                        }
                    });

                    animation1.start();
                }

                root_layout.setOnTouchListener(null);

                return false;
            }
        });




        // Enables Always-on
        setAmbientEnabled();
    }

    protected void onStart() {
        super.onStart();

        final RelativeLayout root_layout = (RelativeLayout) findViewById(R.id.root_layout);



        root_layout.post( new Runnable() {
            @Override
            public void run() {

                //start the sorting
                interaction_button[0].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i < main_screen_traits.getInteraction_button_count(); i++) {
                            interaction_button[i].setVisibility(View.INVISIBLE);
                        }
                        settings_button.setVisibility(View.INVISIBLE);

                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        start_time = System.currentTimeMillis();
                        timer_handler.postDelayed(timerRunnable, 0);

                        start_sort();

                    }
                });

                //change sort left
                interaction_button[1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main_screen_traits.setType_sort(main_screen_traits.getType_sort() - 1);
                        sort_type.setText(main_screen_traits.sorting_algos.getSort_name());
                        change_fg_circle_colors();
                    }
                });

                //change sort right
                interaction_button[2].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        main_screen_traits.setType_sort(main_screen_traits.getType_sort() + 1);
                        sort_type.setText(main_screen_traits.sorting_algos.getSort_name());
                        change_fg_circle_colors();
                    }
                });

                //open settings
                settings_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open_settings2();

                    }
                });
            }
            });

    }

    public void open_settings2(){
        Intent intent = new Intent(this, Settings2.class);
        startActivity(intent);
    }



    public int paused = 0;


    protected void onPause() {
        super.onPause();
        paused = 1;

    }


    protected void onResume() {

        super.onResume();

        if(paused == 1) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            int AT = sharedPref.getInt("AT", 300);
            //int NC = sharedPref.getInt("NC", 5);
            if ((main_screen_traits.getAnimation_time() != AT) ) {
                paused = 0;



                main_screen_traits.setAnimation_time(AT);
                //main_screen_traits.setBg_circle_count(NC);
                //main_screen_traits.setFg_circle_count(NC);


/*
                Intent intent = getIntent();
                finish();
                startActivity(intent);

 */




            }

        }


    }




    public void change_fg_circle_colors() {
        for(int i = 0; i < main_screen_traits.getFg_circle_count(); i++) {
            int reset = android.graphics.Color.argb(255, 255, 255, 255);
            fg_circle[i].getImage_view().setColorFilter(reset, PorterDuff.Mode.MULTIPLY);
            int color = android.graphics.Color.argb(fg_circle[i].getAlpha(), main_screen_traits.sorting_algos.getRed(), main_screen_traits.sorting_algos.getGreen(), main_screen_traits.sorting_algos.getBlue());
            fg_circle[i].getImage_view().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    public void start_sort() {
        if(main_screen_traits.getType_sort() == 0) {
            bubble_sort.start_bubble_sort();
        } else if(main_screen_traits.getType_sort() == 1) {
            bogo_sort.start_bogo_sort();
        } else if(main_screen_traits.getType_sort() == 2) {
            quick_sort.start_quick_sort();
        }
    }

    public class end_class {
        public void on_sort_end() {
            timer_handler.removeCallbacks(timerRunnable);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private static void shuffle_array(int[] array)
    {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }

    }

    public static class bubble_sort {
        private static int[] index = new int[main_screen_traits.getFg_circle_count()];
        private static int current_number;
        private static int comparisons;
        private static int rounds;
        private static int switched;
        private static int safe_passes;

        public static void start_bubble_sort() {
            for(int i = 0; i < index.length; i++) {
                index[i] = i;
            }

            current_number = 0;
            comparisons = 0;
            rounds = 0;
            switched = 0;
            safe_passes = 0;

            call_up();
        }

        private static void call_up() {
            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_number]].getX(), fg_circle[index[current_number]].getY());
            path1.lineTo(placer_circle[1].getX(), placer_circle[1].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_number]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time());


            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animation1.start();

            Path path2 = new Path();
            path2.moveTo(fg_circle[index[current_number + 1]].getX(), fg_circle[index[current_number + 1]].getY());
            path2.lineTo(placer_circle[2].getX(), placer_circle[2].getY());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(fg_circle[index[current_number + 1]].getImage_view(), View.X, View.Y, path2);
            animation2.setDuration(main_screen_traits.getAnimation_time());


            animation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    test_fg_circles();

                }
            });

            animation2.start();
        }

        private static void test_fg_circles() {
            comparisons += 1;
            comp_counter.setText(Integer.toString(comparisons));

            if(fg_circle[index[current_number]].getAlpha() > fg_circle[index[current_number + 1]].getAlpha()) {
                switched = 1;
                switch_fg_circles();
            } else {
                send_back();
            }
        }

        private static void switch_fg_circles() {
            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_number]].getX(), fg_circle[index[current_number]].getY());
            path1.quadTo(placer_circle[0].getX(), placer_circle[0].getY(), placer_circle[2].getX(), placer_circle[2].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_number]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time() * 5 / 6);


            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animation1.start();

            Path path2 = new Path();
            path2.moveTo(fg_circle[index[current_number + 1]].getX(), fg_circle[index[current_number + 1]].getY());
            path2.quadTo(placer_circle[3].getX(), placer_circle[3].getY(), placer_circle[1].getX(), placer_circle[1].getY());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(fg_circle[index[current_number + 1]].getImage_view(), View.X, View.Y, path2);
            animation2.setDuration(main_screen_traits.getAnimation_time() * 5 / 6);


            animation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    send_back();
                }
            });

            animation2.start();
        }

        private static void send_back() {
            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_number]].getX(), fg_circle[index[current_number]].getY());
            path1.lineTo(bg_circle[current_number + switched].getX(), bg_circle[current_number + switched].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_number]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time());


            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animation1.start();

            Path path2 = new Path();
            path2.moveTo(fg_circle[index[current_number + 1]].getX(), fg_circle[index[current_number + 1]].getY());
            path2.lineTo(bg_circle[current_number + 1 - switched].getX(), bg_circle[current_number + 1 - switched].getY());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(fg_circle[index[current_number + 1]].getImage_view(), View.X, View.Y, path2);
            animation2.setDuration(main_screen_traits.getAnimation_time());


            animation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    apply_backend_changes();

                }
            });

            animation2.start();
        }

        private static void apply_backend_changes() {
            //switches the indices
            if(switched == 1) {
                int temp1 = index[current_number];
                int temp2 = index[current_number + 1];
                index[current_number] = temp2;
                index[current_number + 1] = temp1;
            }

            if(current_number == 0) {
                safe_passes = 0;
            }

            if(switched == 1) {
                safe_passes = 0;
            } else {
                safe_passes += 1;
            }

            switched = 0;

            //safely iterates the number of the index
            current_number += 1;

            if(current_number >= main_screen_traits.getFg_circle_count() - 1 - rounds) {
                current_number = 0;
                rounds += 1;
            }

            //check for completion of bubble sort
            if(safe_passes >= main_screen_traits.getFg_circle_count() - 1 - rounds) {
                end_sort.on_sort_end();
            } else {
                call_up();
            }
        }




    }



    public static class bogo_sort {
        private static int comparisons;
        private static int current_number;
        private static int[] index = new int[main_screen_traits.getFg_circle_count()];

        public static void start_bogo_sort() {
            for(int i = 0; i < index.length; i++) {
                index[i] = i;
            }

            comparisons = 0;
            current_number = 0;

            call_up();
        }

        private static void call_up() {
            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_number]].getX(), fg_circle[index[current_number]].getY());
            path1.lineTo(placer_circle[1].getX(), placer_circle[1].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_number]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time());


            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animation1.start();

            Path path2 = new Path();
            path2.moveTo(fg_circle[index[current_number + 1]].getX(), fg_circle[index[current_number + 1]].getY());
            path2.lineTo(placer_circle[2].getX(), placer_circle[2].getY());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(fg_circle[index[current_number + 1]].getImage_view(), View.X, View.Y, path2);
            animation2.setDuration(main_screen_traits.getAnimation_time());


            animation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    test_fg_circles();
                }
            });

            animation2.start();
        }

        private static void test_fg_circles() {
            comparisons += 1;
            comp_counter.setText(Integer.toString(comparisons));

            if(fg_circle[index[current_number]].getAlpha() > fg_circle[index[current_number + 1]].getAlpha()) {
                shuffle_circles();
            } else {
                send_back();
            }
        }

        private static void shuffle_circles() {
            int[] new_index = new int[index.length];

            for(int i = 0; i < new_index.length; i++) {
                new_index[i] = i;
            }

            shuffle_array(new_index);
            final int[] finalNew_index = new_index;
            for(int i = 0; i < index.length; i++) {
                Path path1 = new Path();
                path1.moveTo(fg_circle[new_index[i]].getX(), fg_circle[new_index[i]].getY());
                path1.cubicTo(placer_circle[0].getX(), placer_circle[1].getY(), placer_circle[0].getX(), placer_circle[1].getY(), bg_circle[i].getX(), bg_circle[i].getY());

                ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[new_index[i]].getImage_view(), View.X, View.Y, path1);
                animation1.setDuration(main_screen_traits.getAnimation_time());


                final int finalI = i;
                animation1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(finalI == index.length - 1) {
                            index = finalNew_index;
                            current_number = 0;
                            call_up();
                        }
                    }
                });

                animation1.start();
            }


        }

        private static void send_back() {
            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_number]].getX(), fg_circle[index[current_number]].getY());
            path1.lineTo(bg_circle[current_number].getX(), bg_circle[current_number].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_number]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time());


            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                }
            });

            animation1.start();

            Path path2 = new Path();
            path2.moveTo(fg_circle[index[current_number + 1]].getX(), fg_circle[index[current_number + 1]].getY());
            path2.lineTo(bg_circle[current_number + 1].getX(), bg_circle[current_number + 1].getY());

            ObjectAnimator animation2 = ObjectAnimator.ofFloat(fg_circle[index[current_number + 1]].getImage_view(), View.X, View.Y, path2);
            animation2.setDuration(main_screen_traits.getAnimation_time());


            animation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    current_number += 1;
                    if(current_number >= index.length - 1) {
                        end_sort.on_sort_end();
                    } else {
                        call_up();
                    }
                }
            });

            animation2.start();
        }

    }

    public static class quick_sort {
        private static int comparisons;
        private static int current_pivot;
        private static int[] index = new int[main_screen_traits.getFg_circle_count()];
        private static int[] solved = new int[index.length];
        private static int[] temp_index = new int[index.length];
        private static int lower_bound;
        private static int upper_bound;
        private static int current_comp;
        private static int sent_right;

        public static void start_quick_sort() {
            comparisons = 0;
            current_pivot = 0;
            for(int i = 0; i < index.length; i++) {
                index[i] = i;
                solved[i] = 0;
                temp_index[i] = i;
            }

            call_up_pivot();
        }

        private static void call_up_pivot() {
            lower_bound = current_pivot;
            upper_bound = find_upper_bound();
            current_comp = current_pivot + 1;
            sent_right = 0;

            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_pivot]].getX(), fg_circle[index[current_pivot]].getY());
            path1.lineTo(placer_circle[0].getX(), placer_circle[0].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_pivot]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time());


            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    call_up_others();
                }
            });

            animation1.start();
        }

        private static void call_up_others() {

            for(int i = lower_bound + 1; i <= upper_bound; i++) {
                Path path1 = new Path();
                path1.moveTo(fg_circle[index[i]].getX(), fg_circle[index[i]].getY());
                path1.lineTo(placer_circle[3].getX(), placer_circle[3].getY());

                ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[i]].getImage_view(), View.X, View.Y, path1);
                animation1.setDuration(main_screen_traits.getAnimation_time());


                final int finalI = i;
                animation1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(finalI == upper_bound) {
                            send_left_right();
                        }
                    }
                });

                animation1.start();
            }

        }


        private static int find_upper_bound() {
            for(int i = current_pivot; i < index.length; i++) {
                if(solved[i] == 1) {
                    return(i - 1);
                }
            }
            return(index.length - 1);
        }

        private static void send_left_right() {
            if(current_comp <= upper_bound) {
                if(fg_circle[index[current_comp]].getAlpha() > fg_circle[index[current_pivot]].getAlpha()) {
                    Path path1 = new Path();
                    path1.moveTo(fg_circle[index[current_comp]].getX(), fg_circle[index[current_comp]].getY());
                    path1.lineTo(bg_circle[upper_bound - sent_right].getX(), bg_circle[upper_bound - sent_right].getY());

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_comp]].getImage_view(), View.X, View.Y, path1);
                    animation1.setDuration(main_screen_traits.getAnimation_time());

                    animation1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            temp_index[upper_bound - sent_right] = index[current_comp];


                            current_comp += 1;
                            sent_right += 1;
                            comparisons += 1;
                            comp_counter.setText(Integer.toString(comparisons));
                            send_left_right();
                        }
                    });

                    animation1.start();
                } else {
                    Path path1 = new Path();
                    path1.moveTo(fg_circle[index[current_comp]].getX(), fg_circle[index[current_comp]].getY());
                    path1.lineTo(bg_circle[current_comp - 1 - sent_right].getX(), bg_circle[current_comp - 1 - sent_right].getY());

                    ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_comp]].getImage_view(), View.X, View.Y, path1);
                    animation1.setDuration(main_screen_traits.getAnimation_time());

                    animation1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            temp_index[current_comp - 1 - sent_right] = index[current_comp];


                            current_comp += 1;
                            comparisons += 1;
                            comp_counter.setText(Integer.toString(comparisons));

                            send_left_right();
                        }
                    });

                    animation1.start();
                }
            } else {
                send_back_pivot();
            }
        }

        private static void send_back_pivot() {
            Path path1 = new Path();
            path1.moveTo(fg_circle[index[current_pivot]].getX(), fg_circle[index[current_pivot]].getY());
            path1.lineTo(bg_circle[upper_bound - sent_right].getX(), bg_circle[upper_bound - sent_right].getY());

            ObjectAnimator animation1 = ObjectAnimator.ofFloat(fg_circle[index[current_pivot]].getImage_view(), View.X, View.Y, path1);
            animation1.setDuration(main_screen_traits.getAnimation_time());

            animation1.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    temp_index[upper_bound - sent_right] = index[current_pivot];

                    remake_index();
                }
            });

            animation1.start();
        }

        private static void remake_index() {
            for(int i = 0; i < index.length; i++) {
                index[i] = temp_index[i];
            }



            solved[upper_bound - sent_right] = 1;

            find_solved();

            int sum_of_solved = 0;

            for(int i = 0; i < index.length; i++) {
                if(solved[i] == 1) {
                    sum_of_solved += 1;
                }
            }

            if(sum_of_solved >= index.length - 1) {
                end_sort.on_sort_end();
            } else {
                call_up_pivot();
            }

        }

        private static void find_solved() {
            current_pivot = find_lower_bound();
            if (current_pivot == find_upper_bound()) {
                solved[current_pivot] = 1;
                find_solved();
            }
        }

        private static int find_lower_bound() {
            for(int i = 0; i < index.length; i++) {
                if(solved[i] == 0) {
                    return(i);
                }
            }
            return(index.length - 1);
        }
    }




}
