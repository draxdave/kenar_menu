package ir.drax.kenar_menu;

import android.view.MotionEvent;
import android.view.View;

import ir.drax.kenar_menu.interfaces.Drag;

public class DragHelper {

    private Drag dragListener;
    private float dX;

    DragHelper(){}
    void setListener(Drag listener, View view) {
        this.dragListener = listener;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (event.getRawX()  == dX ) {
                            dragListener.onClick();
                        }else
                            dragListener.onLeave();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        dragListener.onSwipe((event.getRawX() + dX) * -1);

                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    void setListener2(Drag listener, View view) {
        this.dragListener = listener;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (dragListener==null){
                    view.performClick();
                    return false;
                }

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = event.getRawX();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (event.getRawX()  == dX ) {
                            dragListener.onClick();
                        }else
                            dragListener.onLeave();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        dragListener.onSwipe(dX - event.getRawX() );

                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    void disable(View view){
        dragListener = null;
        view.setOnTouchListener(null);
    }

}
