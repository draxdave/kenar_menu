package ir.drax.kenar_menu;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.transition.TransitionManager;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;
import java.util.List;

import ir.drax.kenar_menu.interfaces.Drag;
import ir.drax.kenar_menu.interfaces.SliderPlusInteraction;
import ir.drax.kenar_menu.models.ReserveItem;
import ir.drax.loadingbutton.ClickListener;
import ir.drax.loadingbutton.NormalButton;

public class SliderPlus extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener, Drag {
    private String TAG = getClass().getSimpleName();
    private int DRAWER_STATE_CLOSED = 0;
    private int DRAWER_STATE_OPEN = 1;
    private int DRAWER_STATE_FOCUSED = 2;
    private RecyclerView listView;
    private NormalButton refreshButton ;
    private SwitchCompat just_mine_switch;
    private SwipeRefreshLayout refreshLayout;
    private ListAdapter listAdapter;
    private ArrayList<ReserveItem> items = new ArrayList<>();
    private float LOADING_ALPHA = 0.5f;
    private int drawerState = DRAWER_STATE_CLOSED;
    private AnimatedVectorDrawableCompat animatedVectorDrawableCompat;

    private LinearLayout progressBar;
    private int screenWidth,listItemId;
    private float drawerDoorSize = 0.2f;
    private DragHelper dragHelper=new DragHelper();
    private View innerRoomLayout;
    private SliderPlusInteraction plusInteractions;

    public SliderPlus(Context context) {
        super(context);
        init();
    }

    public SliderPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SliderPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
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
                int widthWas = rightWas - leftWas; // right exclusive, left inclusive
                if( v.getWidth() != widthWas )
                {
                    float alpha =  (((float)v.getWidth()) / screenWidth);
                    //float rotation =  180 - ((((float)v.getWidth()) / (screenWidth*(1-drawerDoorSize)) ) * 180);
                    //Log.e(TAG,rotation +"-");
                    v.setAlpha(1 - alpha);
                    //toggleIV.setRotation(rotation);
                    // width has changed
                }
                int heightWas = bottomWas - topWas; // bottom exclusive, top inclusive
                if( v.getHeight() != heightWas )
                {
                    // height has changed
                }
            }
        });

        dragHelper.setListener2(this, findViewById(R.id.faded_back_btn));
        new DragHelper().setListener(new Drag() {
            @Override
            public void onSwipe(float position) {
                Log.e(TAG,position +"-");

                ConstraintLayout container = findViewById(R.id.nvLeft);

                ConstraintSet set = new ConstraintSet();
                set.clone(container);

                set.setGuidelineEnd(R.id.endingBoundary, (int) (-position + findViewById(R.id.toggleDrawer).getWidth()));


                set.applyTo(container);
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
                drawerState = DRAWER_STATE_CLOSED;
                openDrawer();
            }
        }, findViewById(R.id.toggleDrawer));

        /*findViewById(R.id.edgeTouchView).setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.e(TAG,"onLongClick");
                onSwipeOut(40);
                return false;
            }
        });*/
    }


    private void initFilter() {
        just_mine_switch = findViewById(R.id.just_mine_switch);
        just_mine_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onRefresh();
            }
        });
    }

    public void fillListByPage(List<ReserveItem> newItems , int page){
        refreshListDone();
        if (page==1) items.clear();

        items.addAll(newItems);
        if (listAdapter != null)listAdapter.updateList();
    }

    public void showRoomProgress(boolean show){
        if (show) {
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
            public View onItemLayoutLoaded(ReserveItem item, int position, int listItemLayout) {
                if (plusInteractions!=null) {
                    return plusInteractions.listItemLayout(item,position,LayoutInflater.from(getContext())
                            .inflate( listItemId, null, false));
                }
                return null;
            }

            @Override
            public void onItemLayoutLoaded(ReserveItem reserveItem, int pos) {
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

        onRefresh();

        dragHelper.setListener(this,this);
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

        set.applyTo(container);

        dragHelper.disable(this);

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

        set.setGuidelineBegin(R.id.StartingBoundary, (int) (position + (screenWidth * drawerDoorSize)));


        set.applyTo(container);

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

    public SliderPlus setPlusInteractions(SliderPlusInteraction plusInteractions) {
        this.plusInteractions = plusInteractions;
        return this;
    }

    public SliderPlus setInnerRoomLayout(View innerRoomLayout) {
        if (this.innerRoomLayout !=null)
            ((ViewGroup)findViewById(R.id.innerRoomContainer)).removeView(this.innerRoomLayout );

        this.innerRoomLayout = innerRoomLayout;
        ((ViewGroup)findViewById(R.id.innerRoomContainer)).addView(this.innerRoomLayout );

        return this;
    }


    public SliderPlus setListItemLayoutId(int listItemId) {
        this.listItemId = listItemId;
        buildAdapter();
        return this;
    }

    public void open() {
        openDrawer();
    }

    public void close() {
        closeDrawer();
    }

    public ArrayList<ReserveItem> getHiddenItems(){
        return listAdapter.getHiddenItems();
    }
}
