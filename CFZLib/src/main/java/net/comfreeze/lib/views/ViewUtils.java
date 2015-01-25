package net.comfreeze.lib.views;

import android.annotation.TargetApi;
import android.gesture.GestureOverlayView;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterViewFlipper;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.StackView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import net.comfreeze.lib.CFZApplication;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @author james
 * @version %I%
 * @package ComFreeze Android Tools
 * @serial 8/3/13
 */
public class ViewUtils {
    public static final String TAG = ViewUtils.class.getSimpleName();
    public static Set<EVENT_LOCATION> mask = new HashSet<EVENT_LOCATION>();

    @TargetApi(11)
    public static boolean inBox(int[] lrtb, DragEvent event) {
        Rect target = new Rect(lrtb[0], lrtb[2], lrtb[1], lrtb[3]);
        int x = (int) event.getX();
        int y = (int) event.getY();
//		CFZApplication.LOG.d(TAG, "EVENT TEST: BOUNDARIES: " + target + " CONTAINS " + x + " x " + y);
        return target.contains(x, y);
    }

    public static boolean inBox(int[] lrtb, MotionEvent event) {
        Rect target = new Rect(lrtb[0], lrtb[2], lrtb[1], lrtb[3]);
        int x = (int) event.getX();
        int y = (int) event.getY();
//		CFZApplication.LOG.d(TAG, "EVENT TEST: BOUNDARIES: " + target + " CONTAINS " + x + " x " + y);
        return target.contains(x, y);
    }

    public static EVENT_LOCATION locationInBox(int x, int y, Rect box) {
        return locationInBox(null, x, y, box, 3, 3);
    }

    public static EVENT_LOCATION locationInBox(Set<EVENT_LOCATION> mask, int x, int y, Rect box) {
        return locationInBox(mask, x, y, box, 3, 3);
    }

    private static boolean allowed(Set<EVENT_LOCATION> mask, EVENT_LOCATION t) {
        if (null != mask && mask.size() > 0) {
            if (mask.contains(t))
                return true;
            return false;
        }
        return true;
    }

    public static EVENT_LOCATION locationInBox(int x, int y, Rect box, int divX, int divY) {
        return locationInBox(null, x, y, box, divX, divY);
    }

    public static EVENT_LOCATION locationInBoxCorners(int x, int y, Rect box) {
        HashSet<EVENT_LOCATION> m = new HashSet<EVENT_LOCATION>();
        m.add(EVENT_LOCATION.TOP_LEFT);
        m.add(EVENT_LOCATION.TOP_RIGHT);
        m.add(EVENT_LOCATION.BOTTOM_RIGHT);
        m.add(EVENT_LOCATION.BOTTOM_LEFT);
        return locationInBox(null, x, y, box, 3, 2);
    }

    public static EVENT_LOCATION locationInBoxSides(int x, int y, Rect box) {
        HashSet<EVENT_LOCATION> m = new HashSet<EVENT_LOCATION>();
        m.add(EVENT_LOCATION.TOP);
        m.add(EVENT_LOCATION.BOTTOM);
        m.add(EVENT_LOCATION.RIGHT);
        m.add(EVENT_LOCATION.LEFT);
        return locationInBox(null, x, y, box, 3, 2);
    }

    public static EVENT_LOCATION locationInBoxTopBottom(int x, int y, Rect box) {
        HashSet<EVENT_LOCATION> m = new HashSet<EVENT_LOCATION>();
        m.add(EVENT_LOCATION.TOP);
        m.add(EVENT_LOCATION.BOTTOM);
        return locationInBox(null, x, y, box, 3, 2);
    }

    public static EVENT_LOCATION locationInBoxLeftRight(int x, int y, Rect box) {
        HashSet<EVENT_LOCATION> m = new HashSet<EVENT_LOCATION>();
        m.add(EVENT_LOCATION.RIGHT);
        m.add(EVENT_LOCATION.LEFT);
        return locationInBox(null, x, y, box, 3, 2);
    }

    public static EVENT_LOCATION locationInBox(Set<EVENT_LOCATION> mask, int x, int y, Rect box, int divX, int divY) {
        int spanX = (box.width() / divX);
        int spanY = (box.height() / divY);
        Rect inset = new Rect(box);
        inset.inset(spanX, spanY);
//		CFZApplication.LOG.d(TAG, "RECT TARGET: " + x + "x" + y);
//		CFZApplication.LOG.d(TAG, "RECT OUTER: " + box);
//		CFZApplication.LOG.d(TAG, "RECT INSET: " + inset);
        if (box.contains(x, y)) {
            if (allowed(mask, EVENT_LOCATION.CENTER)
            /* */ && inset.contains(x, y))
                return EVENT_LOCATION.CENTER;
            else if (allowed(mask, EVENT_LOCATION.TOP_LEFT)
            /* */ && new Rect(box.left, box.top, inset.left, inset.top).contains(x, y))
                return EVENT_LOCATION.TOP_LEFT;
            else if (allowed(mask, EVENT_LOCATION.BOTTOM_LEFT)
            /* */ && new Rect(box.left, inset.bottom, inset.left, box.bottom).contains(x, y))
                return EVENT_LOCATION.BOTTOM_LEFT;
            else if (allowed(mask, EVENT_LOCATION.TOP_RIGHT)
            /* */ && new Rect(inset.right, box.top, box.right, inset.top).contains(x, y))
                return EVENT_LOCATION.TOP_RIGHT;
            else if (allowed(mask, EVENT_LOCATION.BOTTOM_RIGHT)
            /* */ && new Rect(inset.right, inset.bottom, box.right, box.bottom).contains(x, y))
                return EVENT_LOCATION.BOTTOM_RIGHT;
            else if (allowed(mask, EVENT_LOCATION.TOP)
            /* */ && new Rect(box.left, box.top, box.right, inset.top).contains(x, y))
                return EVENT_LOCATION.TOP;
            else if (allowed(mask, EVENT_LOCATION.BOTTOM)
            /* */ && new Rect(box.left, inset.bottom, box.right, box.bottom).contains(x, y))
                return EVENT_LOCATION.BOTTOM;
            else if (allowed(mask, EVENT_LOCATION.LEFT)
            /* */ && new Rect(box.left, box.top, inset.left, box.bottom).contains(x, y))
                return EVENT_LOCATION.LEFT;
            else if (allowed(mask, EVENT_LOCATION.RIGHT)
            /* */ && new Rect(inset.right, box.top, box.right, box.bottom).contains(x, y))
                return EVENT_LOCATION.RIGHT;
        }
        return EVENT_LOCATION.NONE;
    }

    public static Pair<Integer, EVENT_LOCATION> getEventPosition(ViewGroup view, int x, int y) {
        return getEventPosition(view, x, y, 6, 9, 3, 3);
    }

    public static Pair<Integer, EVENT_LOCATION> getEventPosition(ViewGroup view, int x, int y, int divX, int divY, int childDivX, int childDivY) {
        Pair<Integer, EVENT_LOCATION> result = new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.NONE);
        int left = view.getLeft();
        int right = view.getRight();
        int top = view.getTop();
        int bottom = view.getBottom();
        EVENT_LOCATION loc = locationInBox(x, y, new Rect(
            /*   */left
            /* */, top
            /* */, right
            /* */, bottom
        ), divX, divY);
        if (!EVENT_LOCATION.NONE.equals(loc)) {
            for (int i = 0; i < view.getChildCount(); i++) {
                View child = view.getChildAt(i);
                loc = locationInBox(x, y, new Rect(
                    /*   */left + child.getLeft()
                    /* */, top + child.getTop()
                    /* */, left + child.getRight()
                    /* */, top + child.getBottom()
                ), childDivX, childDivY);
//				CFZApplication.LOG.d(TAG, "RECT LOC: [" + i + "] " + loc.name());
                if (!EVENT_LOCATION.NONE.equals(loc))
                    return new Pair<Integer, EVENT_LOCATION>(i, loc);
            }
        }
        return result;
    }

    public static Pair<Integer, EVENT_LOCATION> getAdapterPosition(ListView view, int x, int y) {
        Pair<Integer, EVENT_LOCATION> result = getEventPosition(view, x, y);
        int offset = view.getFirstVisiblePosition();
        if (result.first > -1)
            return new Pair<Integer, EVENT_LOCATION>(result.first + offset, result.second);
        return result;
    }

    public static Pair<Integer, EVENT_LOCATION> getAdapterPosition(ListView view, int x, int y, int divX, int divY, int childDivX, int childDivY) {
        Pair<Integer, EVENT_LOCATION> result = getEventPosition(view, x, y, divX, divY, childDivX, childDivY);
        int offset = view.getFirstVisiblePosition();
        if (result.first > -1)
            return new Pair<Integer, EVENT_LOCATION>(result.first + offset, result.second);
        return result;
    }

    public static int getEventPosition(ViewGroup view, MotionEvent event) {
        int result = 0;
        float eX = event.getX();
        float eY = event.getY();
        if (null != view) {
            int left = view.getLeft();
            int right = view.getRight();
            int top = view.getTop();
            int bottom = view.getBottom();
            for (int i = 0; i < view.getChildCount(); i++) {
                View child = view.getChildAt(i);
                CFZApplication.LOG.d(TAG, "EVENT TEST: CHILD[" + i + "]: L:" + child.getLeft() + " R:" + child.getRight() + " T:" + child.getTop() + " B:" + child.getBottom());
                if (ViewUtils.inBox(new int[]{
				/*   */child.getLeft() + left
				/* */, child.getRight() + left
				/* */, child.getTop() + top
				/* */, child.getTop() + child.getHeight() / 2 + top
                }, event))
                    return i;
                if (ViewUtils.inBox(new int[]{
				/*   */child.getLeft() + left
				/* */, child.getRight() + left
				/* */, child.getTop() + child.getHeight() / 2 + top
				/* */, child.getBottom() + top
                }, event))
                    return i + 1;
            }
            if (ViewUtils.inBox(new int[]{
				/*   */left
				/* */, right
				/* */, top
				/* */, bottom
            }, event)) {
                if (view.getChildCount() > 0) {
                    int y = (int) event.getY();
                    int maxY = view.getChildAt(view.getChildCount() - 1).getBottom();
                    int minY = view.getChildAt(0).getTop();
                    if (y < minY)
                        return 0;
                    else if (y > maxY)
                        return view.getChildCount();
                } else
                    return 0;
            }
        } else {
            CFZApplication.LOG.e(TAG, "View was null!");
        }
        return result;
    }

    public static Pair<Integer, EVENT_LOCATION> getSpecificEventPosition(ViewGroup view, MotionEvent event) {
        Pair<Integer, EVENT_LOCATION> result = new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.NONE);
        float eX = event.getX();
        float eY = event.getY();
        if (null != view) {
            int left = view.getLeft();
            int right = view.getRight();
            int top = view.getTop();
            int bottom = view.getBottom();
            for (int i = 0; i < view.getChildCount(); i++) {
                View child = view.getChildAt(i);
//				CFZApplication.LOG.d(TAG, "EVENT TEST: CHILD[" + i + "]: L:" + child.getLeft() + " R:" + child.getRight() + " T:" + child.getTop() + " B:" + child.getBottom());
                if (ViewUtils.inBox(new int[]{
				/*   */child.getLeft() + left
				/* */, (child.getLeft() + child.getWidth() / 4) + left
				/* */, child.getTop() + top
				/* */, child.getTop() + child.getHeight() + top
                }, event))
                    return new Pair<Integer, EVENT_LOCATION>(i, EVENT_LOCATION.LEFT);
                if (ViewUtils.inBox(new int[]{
				/*   */child.getLeft() + left
				/* */, (child.getRight() - child.getWidth() / 4) + left
				/* */, child.getTop() + top
				/* */, child.getTop() + child.getHeight() + top
                }, event))
                    return new Pair<Integer, EVENT_LOCATION>(i, EVENT_LOCATION.RIGHT);
                if (ViewUtils.inBox(new int[]{
				/*   */child.getLeft() + left
				/* */, child.getRight() + left
				/* */, child.getTop() + top
				/* */, child.getTop() + child.getHeight() / 2 + top
                }, event))
                    return new Pair<Integer, EVENT_LOCATION>(i, EVENT_LOCATION.TOP);
                if (ViewUtils.inBox(new int[]{
				/*   */child.getLeft() + left
				/* */, child.getRight() + left
				/* */, child.getTop() + child.getHeight() / 2 + top
				/* */, child.getBottom() + top
                }, event))
                    return new Pair<Integer, EVENT_LOCATION>(i, EVENT_LOCATION.BOTTOM);
            }
            if (ViewUtils.inBox(new int[]{
				/*   */left
				/* */, right
				/* */, top
				/* */, bottom
            }, event)) {
                if (view.getChildCount() > 0) {
                    int y = (int) event.getY();
                    int x = (int) event.getX();
                    int maxY = view.getChildAt(view.getChildCount() - 1).getBottom();
                    int minY = view.getChildAt(0).getTop();
                    int maxX = view.getRight() - 20;
                    int minX = view.getLeft() + 20;
                    if (y < minY)
                        return new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.TOP);
                    else if (y > maxY)
                        return new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.BOTTOM);
                    else if (x < minX)
                        return new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.LEFT);
                    else if (x > maxX)
                        return new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.RIGHT);
                } else
                    return new Pair<Integer, EVENT_LOCATION>(-1, EVENT_LOCATION.CENTER);
            }
        } else {
            CFZApplication.LOG.e(TAG, "View was null!");
        }
        return result;
    }

    public static enum EVENT_LOCATION {
        NONE, TOP, RIGHT, BOTTOM, LEFT, CENTER, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT, TOP_LEFT
    }

    public static class ViewHolder {
        protected LinkedHashMap<Integer, View> children = new LinkedHashMap<Integer, View>();
        protected ViewGroup root;

        public static <T extends ViewHolder> T factory(Class clazz, ViewGroup view) {
            ViewHolder holder = null;
            try {
                holder = (ViewHolder) clazz.newInstance();
                view.setTag(holder);
                holder.root = view;
                holder.map(view, 0);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return (T) holder;
        }

        public Integer[] getChildIds() {
            return children.keySet().toArray(new Integer[children.size()]);
        }

        public <T extends ViewHolder> T map(ViewGroup view, int level) {
            String prefix = "";
            for (int p = 0; p < level; p++) {
                prefix += "--";
            }
//			CFZApplication.LOG.d(TAG, prefix + "Mapping view elements");
            int numChildren = view.getChildCount();
            if (null == children)
                children = new LinkedHashMap<Integer, View>();
            for (int i = 0; i < numChildren; i++) {
                View child = view.getChildAt(i);
                children.put(child.getId(), child);
                if (child instanceof ViewGroup)
//					CFZApplication.LOG.d(TAG, prefix + child.getClass().getSimpleName() + " {");
                    map((ViewGroup) child, level + 1); // Recurse
//					CFZApplication.LOG.d(TAG, prefix + "}");
//				} else
//					CFZApplication.LOG.d(TAG, prefix + child.getClass().getSimpleName());
            }
            return (T) this;
        }

        public <T extends ViewHolder> T tag(Object tag) {
            root.setTag(tag);
            return (T) this;
        }

        public <T extends ViewHolder> T tag(int id, Object tag) {
            root.setTag(id, tag);
            return (T) this;
        }

        public <T extends ViewHolder> T tagChild(int child, Object tag) {
            if (null != get(child))
                get(child).setTag(tag);
            return (T) this;
        }

        public <T extends ViewHolder> T tagChild(int child, int id, Object tag) {
            if (null != get(child))
                get(child).setTag(id, tag);
            return (T) this;
        }

        public View get(int id) {
            if (null == children && null != root)
                map(root, 0);
            if (null != children && children.containsKey(id))
                return children.get(id);
            return null;
        }

        public TextView getText(int id) {
            return (TextView) get(id);
        }

        public EditText getEdit(int id) {
            return (EditText) get(id);
        }

        public ImageView getImage(int id) {
            return (ImageView) get(id);
        }

        public FrameLayout getFrame(int id) {
            return (FrameLayout) get(id);
        }

        public RelativeLayout getRelative(int id) {
            return (RelativeLayout) get(id);
        }

        public LinearLayout getLinear(int id) {
            return (LinearLayout) get(id);
        }

        public Button getButton(int id) {
            return (Button) get(id);
        }

        public GridView getGrid(int id) {
            return (GridView) get(id);
        }

        public GridLayout getGridLayout(int id) {
            return (GridLayout) get(id);
        }

        public ScrollView getScroll(int id) {
            return (ScrollView) get(id);
        }

        public ProgressBar getProgress(int id) {
            return (ProgressBar) get(id);
        }

        public SeekBar getSeek(int id) {
            return (SeekBar) get(id);
        }

        public ViewGroup getGroup(int id) {
            return (ViewGroup) get(id);
        }

        public TableLayout getTable(int id) {
            return (TableLayout) get(id);
        }

        public TableRow getRow(int id) {
            return (TableRow) get(id);
        }

        public ImageButton getImageButton(int id) {
            return (ImageButton) get(id);
        }

        public RadioGroup getRadioGroup(int id) {
            return (RadioGroup) get(id);
        }

        public RadioButton getRadio(int id) {
            return (RadioButton) get(id);
        }

        public CheckBox getCheckBox(int id) {
            return (CheckBox) get(id);
        }

        public Spinner getSpinner(int id) {
            return (Spinner) get(id);
        }

        public RatingBar getRating(int id) {
            return (RatingBar) get(id);
        }

        public WebView getWebView(int id) {
            return (WebView) get(id);
        }

        public ListView getList(int id) {
            return (ListView) get(id);
        }

        public TabHost getTabHost(int id) {
            return (TabHost) get(id);
        }

        public ViewPager getPager(int id) {
            return (ViewPager) get(id);
        }

//		public TextClock getClock(int id) {
//		    return (TextClock) get(id);
//		}

        public AnalogClock getAnalogClock(int id) {
            return (AnalogClock) get(id);
        }

        public Chronometer getChronometer(int id) {
            return (Chronometer) get(id);
        }

        public DatePicker getDate(int id) {
            return (DatePicker) get(id);
        }

        public TimePicker getTime(int id) {
            return (TimePicker) get(id);
        }

        public CalendarView getCalendar(int id) {
            return (CalendarView) get(id);
        }

        public GestureOverlayView getGesture(int id) {
            return (GestureOverlayView) get(id);
        }

        public TextureView getTexture(int id) {
            return (TextureView) get(id);
        }

        public SurfaceView getSurface(int id) {
            return (SurfaceView) get(id);
        }

        public ViewSwitcher getSwitcher(int id) {
            return (ViewSwitcher) get(id);
        }

        public ViewFlipper getFlipper(int id) {
            return (ViewFlipper) get(id);
        }

        public TextSwitcher getTextSwitcher(int id) {
            return (TextSwitcher) get(id);
        }

        public ImageSwitcher getImageSwitcher(int id) {
            return (ImageSwitcher) get(id);
        }

        public AdapterViewFlipper getAdapterFlipper(int id) {
            return (AdapterViewFlipper) get(id);
        }

        public StackView getStack(int id) {
            return (StackView) get(id);
        }

        public NumberPicker getPicker(int id) {
            return (NumberPicker) get(id);
        }

        public ViewAnimator getAnimator(int id) {
            return (ViewAnimator) get(id);
        }
    }

    public static class GenericViewHolder extends ViewHolder {
        public static GenericViewHolder build(ViewGroup view) {
            return factory(GenericViewHolder.class, view);
        }
    }
}
