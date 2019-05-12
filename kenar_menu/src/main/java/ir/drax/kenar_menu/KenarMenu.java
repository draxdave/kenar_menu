package ir.drax.kenar_menu;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.transition.TransitionManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.drax.kenar_menu.interfaces.Drag;
import ir.drax.kenar_menu.interfaces.SliderPlusInteraction;
import ir.drax.loadingbutton.ClickListener;
import ir.drax.loadingbutton.NormalButton;

public class KenarMenu<T> extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, Drag {
    private String TAG = getClass().getSimpleName();
    private int DRAWER_STATE_CLOSED = 0, DRAWER_STATE_OPEN = 1, DRAWER_STATE_FOCUSED = 2, screenWidth, listItemId;
    private int drawerState = DRAWER_STATE_CLOSED;
    private RecyclerView listView;
    private NormalButton refreshButton ;
    private SwitchCompat filter_switch;
    private SwipeRefreshLayout refreshLayout;
    private ListAdapter listAdapter;
    private ArrayList<Object> items = new ArrayList<>();
    private float LOADING_ALPHA = 0.5f,drawerDoorSize = 0.2f;
    private AnimatedVectorDrawableCompat animatedVectorDrawableCompat;

    private LinearLayout progressBar;
    private DragHelper dragHelper=new DragHelper();
    private View innerRoomLayout;
    private SliderPlusInteraction plusInteractions;
    private boolean roomDataLoaded=false;

    public KenarMenu(Context context) {
        super(context);
        init();
    }

    public KenarMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KenarMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sider_layout, this, true);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;

        initRecycler();
        initRefreshBtn();
        initFilter();
        initToggle();
        initDetails();
    }

    private void initDetails() {
        progressBar = findViewById(R.id.loading);
    }

    private void initToggle() {
        findViewById(R.id.faded_back_btn).addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange( View v, int left, int top, int right, int bottom,
                                        int leftWas, int topWas, int rightWas, int bottomWas )
            {
                /*int widthWas = rightWas - leftWas; // right exclusive, left inclusive
                if( v.getWidth() != widthWas )
                {
                    float alpha =  (((float)v.getWidth()) / screenWidth);
                    //float rotation =  180 - ((((float)v.getWidth()) / (screenWidth*(1-drawerDoorSize)) ) * 180);
                    Log.e(TAG,alpha +"-");
                    v.setAlpha(1 - alpha);
                    //toggleIV.setRotation(rotation);
                    // width has changed
                }
                int heightWas = bottomWas - topWas; // bottom exclusive, top inclusive
                if( v.getHeight() != heightWas )
                {
                    // height has changed
                }*/
            }
        });


        new DragHelper().setListener(new Drag() {
            @Override
            public void onSwipe(float position) {


                ConstraintLayout container = findViewById(R.id.nvLeft);

                ConstraintSet set = new ConstraintSet();
                set.clone(container);

                int end = 0;
                end = (int) (end + position);

                if (ViewCompat.getLayoutDirection(KenarMenu.this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                    end -= findViewById(R.id.toggleDrawer).getWidth();
                    end *= -1;

                }else{
                    end = end + screenWidth;
                }

                Log.e(TAG,position +"="+ end);

                set.setGuidelineEnd(R.id.endingBoundary,  end);


                set.applyTo(container);

                float alpha =  (((float)findViewById(R.id.faded_back_btn)
                        .getWidth()) / screenWidth);
                findViewById(R.id.faded_back_btn).setAlpha(1-alpha);
            }

            @Override
            public void onLeave() {
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) findViewById(R.id.endingBoundary).getLayoutParams();

                if (lp.guideEnd> screenWidth/3 ){
                    drawerState = DRAWER_STATE_CLOSED;
                    openDrawer();

                } else{
                    drawerState = DRAWER_STATE_OPEN;
                    closeDrawer();
                }
            }

            @Override
            public void onClick() {

            }
        }, findViewById(R.id.toggleDrawer));
    }


    private void initFilter() {
        filter_switch = findViewById(R.id.just_mine_switch);
        filter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onRefresh();
            }
        });
    }

    public KenarMenu fillListByPage(List<Object> newItems , int page){
        refreshListDone();
        if (page==1) items.clear();

        items.addAll(newItems);
        if (listAdapter != null)listAdapter.updateList();

        return this;
    }

    public void showRoomProgress(boolean show){
        if (show && !roomDataLoaded) {
            progressBar.setAlpha(0);
            progressBar.setVisibility(VISIBLE);
            progressBar.animate()
                    .alpha(1)
                    .start();
        }else {
            progressBar.setAlpha(1);
            progressBar.setVisibility(VISIBLE);
            progressBar.animate()
                    .alpha(0)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(GONE);
                        }
                    })
                    .start();

        }
    }

    private void initRefreshBtn() {
        refreshButton = findViewById(R.id.refreshBtn);
        refreshButton.setClickListener(new ClickListener() {
            @Override
            public void clicked() {
                onRefresh();
            }
        });
    }

    private void initRecycler() {
        refreshLayout= findViewById(R.id.swipe_container);
        refreshLayout.setOnRefreshListener(this);
        listView = findViewById(R.id.list);
        listView .setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listView .setLayoutManager(layoutManager);
        if (listItemId !=0)
            buildAdapter();

    }

    private void buildAdapter() {
        listAdapter = new ListAdapter(items , listItemId,new ListAdapter.RecyclerStates() {
            @Override
            public void onListEmpty() {
                emptyListMessage(true);
            }
            @Override
            public void onListNotEmpty() {
                emptyListMessage(false);
            }

            @Override
            public View onItemLayoutLoaded(Object item, int position, int listItemLayout) {
                if (plusInteractions!=null) {
                    return plusInteractions.listItemLayout(item,position,LayoutInflater.from(getContext())
                            .inflate( listItemId, null, false));
                }
                return null;
            }

            @Override
            public void onItemLayoutLoaded(Object reserveItem, int pos) {
                if (plusInteractions!=null) {
                    if (plusInteractions.onListItemClicked(reserveItem,pos)){
                        moveToEndDrawer();
                        showRoomProgress(true);
                    }
                }
            }
        });
        listView.setAdapter(listAdapter);
    }


    @Override
    public void onRefresh() {
        if (plusInteractions!=null) {
            refreshButton.disable();
            refreshLayout.setRefreshing(true);
            listView.setAlpha(LOADING_ALPHA);
            if (listAdapter != null)listAdapter.disableInteractions();

            plusInteractions.onRequestListByPage(1);
        }
    }

    private void refreshListDone(){
        if (listAdapter != null)listAdapter.enableInteractions();
        listView.setAlpha(1f);
        refreshButton.enable();
        refreshLayout.setRefreshing(false);
    }

    private void emptyListMessage(boolean show){
        if (show) {
            findViewById(R.id.empty_list_message).setVisibility(VISIBLE);
        }
        else{
            findViewById(R.id.empty_list_message).setVisibility(GONE);
        }
    }

    private void openDrawer(){
        if (drawerState==DRAWER_STATE_OPEN)return;

        ConstraintLayout container = findViewById(R.id.nvLeft);

        TransitionManager.beginDelayedTransition(this);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(R.id.drawerContent, ConstraintSet.START,
                R.id.StartingBoundary, ConstraintSet.END);
        set.connect(R.id.drawerContent, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);

        set.connect(R.id.faded_back_btn, ConstraintSet.END,
                R.id.drawerContent, ConstraintSet.START);

        set.setGuidelinePercent(R.id.StartingBoundary, drawerDoorSize);

        //set.setVisibility(R.id.faded_back_btn, ConstraintSet.VISIBLE);
        set.applyTo(container);

        findViewById(R.id.faded_back_btn).setAlpha(1- drawerDoorSize);

        if (listAdapter.getItemCount()==0)onRefresh();

        dragHelper.setListener(this,this);
        dragHelper.setListener2(this, findViewById(R.id.faded_back_btn));
        drawerState = DRAWER_STATE_OPEN;
    }

    private void closeDrawer(){
        if (drawerState == DRAWER_STATE_CLOSED)return;

        ConstraintLayout container = findViewById(R.id.nvLeft);

        TransitionManager.beginDelayedTransition(this);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(R.id.drawerContent, ConstraintSet.START,
                R.id.endingBoundary, ConstraintSet.END);

        set.connect(R.id.drawerContent, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);

        set.setGuidelinePercent(R.id.StartingBoundary, drawerDoorSize);
        set.setGuidelineEnd(R.id.endingBoundary, 0);

        //set.setVisibility(R.id.faded_back_btn, ConstraintSet.GONE);

        findViewById(R.id.faded_back_btn)
                .animate()
                .alpha(0)
                .setDuration(500)
                .start();

        set.applyTo(container);

        dragHelper.disable(this);
        dragHelper.disable(findViewById(R.id.faded_back_btn));
        drawerState = DRAWER_STATE_CLOSED;

    }

    private void moveToEndDrawer(){

        ConstraintLayout container = findViewById(R.id.nvLeft);

        TransitionManager.beginDelayedTransition(this);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(R.id.drawerContent, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(R.id.drawerContent, ConstraintSet.END,
                R.id.StartingBoundary, ConstraintSet.START);

        set.setGuidelinePercent(R.id.StartingBoundary, drawerDoorSize);

        set.connect(R.id.faded_back_btn, ConstraintSet.END,
                R.id.StartingBoundary, ConstraintSet.START);
        set.applyTo(container);

        findViewById(R.id.innerRoomContainer).scrollTo(0,0);

        drawerState = DRAWER_STATE_FOCUSED;
    }

    @Override
    public void onClick() {
        if (drawerState==DRAWER_STATE_OPEN){
            closeDrawer();

        } else if (drawerState==DRAWER_STATE_CLOSED){
            openDrawer();

        }else if (drawerState==DRAWER_STATE_FOCUSED){
            openDrawer();

        }
    }


    @Override
    public void onSwipe(float position) {
        ConstraintLayout container = findViewById(R.id.nvLeft);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        //set.setGuidelineBegin(R.id.StartingBoundary, (int) (position + (screenWidth * drawerDoorSize)));

        set.setGuidelineBegin(R.id.StartingBoundary, (int) (
                (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL?1:-1)
                        * position + (screenWidth * drawerDoorSize)));


        set.applyTo(container);


        float alpha =  (((float)findViewById(R.id.faded_back_btn)
                .getWidth()) / screenWidth);
        findViewById(R.id.faded_back_btn).setAlpha(1-alpha);
    }

    @Override
    public void onLeave() {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) findViewById(R.id.StartingBoundary).getLayoutParams();

        if (lp.guideBegin > screenWidth/2 ){
            if (drawerState == DRAWER_STATE_FOCUSED){
                openDrawer();
            }else
                closeDrawer();

        } else{
            if (drawerState == DRAWER_STATE_FOCUSED){
                moveToEndDrawer();

            }else {
                drawerState = DRAWER_STATE_CLOSED;
                openDrawer();
            }
        }
    }

    public KenarMenu setPlusInteractions(SliderPlusInteraction plusInteractions) {
        this.plusInteractions = plusInteractions;
        return this;
    }

    public KenarMenu setInnerRoomLayout(View innerRoomLayout) {
        if (this.innerRoomLayout !=null)
            ((ViewGroup)findViewById(R.id.innerRoomContainer)).removeView(this.innerRoomLayout );

        this.innerRoomLayout = innerRoomLayout;
        ((ViewGroup)findViewById(R.id.innerRoomContainer)).addView(this.innerRoomLayout );
        return this;
    }

    public KenarMenu setListItemLayoutId(int listItemId) {
        this.listItemId = listItemId;
        buildAdapter();
        return this;
    }

    public void open() {
        openDrawer();
    }

    public void close() {
        if (drawerState==DRAWER_STATE_FOCUSED)
            openDrawer();

        else
            closeDrawer();
    }

    public ArrayList<Object> getHiddenItems(){
        return listAdapter.getHiddenItems();
    }

    public void showSnackMessage(boolean success , String message){
        Snackbar snackbar = Snackbar.make(this,message,Snackbar.LENGTH_SHORT);

        TextView mesage = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);

        if (success)
            mesage.setTextColor(getResources().getColor(R.color.white));
        else
            mesage.setTextColor(getResources().getColor(R.color.red_color));

        snackbar.show();
    }

    public boolean isOpen(){
        return drawerState != DRAWER_STATE_CLOSED;
    }

    public void roomDataLoaded(){
        if (progressBar.getVisibility()==VISIBLE)
            showRoomProgress(false);

        roomDataLoaded=true;
    }

}
