package cracker.example.com.chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cracker on 2018/7/9.
 */

public class ChessView extends View {

    private Paint myPaint = new Paint();//画笔

    private int myPanelWidth; //棋盘的宽度
    private float myLineHeight;//行高
    private int MAX_LINE = 10;//行数

    private Bitmap myWhitePiece;//白色棋子
    private Bitmap myBlackPiece;//黑色棋子

    //    private onGameListener onGameListener;//回调接口
    private int mUnder;//dialog的Y坐标

    //棋子为行宽的3/4
    private float ratioPieceOfLineHight = 3 * 1.0f / 4;

    private boolean isGameOver;
    private boolean isWhiteWinner;

    private boolean isWhite = true;  //判断是否是白棋先手，或当前为白棋下子
    private List<Point> myWhiteArray = new ArrayList<>();//储存白棋的位置
    private List<Point> myBlackArray = new ArrayList<>();//储存黑棋的位置

    private onGameListener onGameListener;  //回调接口
    private int MAX_PIECE_LINE = 5;
    public static int WHITE_WIN = 0;  //胜利为白方标志
    public static int BLACK_WIN = 1;  //胜利为黑方标志
    public static int PING_JU = 2;     //平局
    public ChessView(Context context) {
        this(context, null);
    }

    // 用于回调的接口
    public interface onGameListener {
        void onGameOver(int i);
    }

    //自定义接口，用于显示dialog
    public void setOnGameListener(ChessView.onGameListener onGameListener) {
        this.onGameListener = onGameListener;
    }

    //棋盘背景颜色
    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0xffffff);
        init();
    }


    private void init() {
        myPaint.setColor(Color.BLACK);
        myPaint.setAntiAlias(true);//设置画笔是否使用抗锯齿
        myPaint.setDither(true);//设置画笔是否防抖动
        myPaint.setStyle(Paint.Style.STROKE);//使用描边

        myWhitePiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_w2);
        myBlackPiece = BitmapFactory.decodeResource(getResources(), R.mipmap.stone_b1);
    }

    //计算布局的大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            //MeasureSpec.UNSPECIFIED表示未知大小
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        myPanelWidth = w;
        myLineHeight = myPanelWidth * 1.0f / MAX_LINE;

        mUnder = h - (h - myPanelWidth) / 2;
        //棋子大小占行宽的3/4
        int pieceWidth = (int) (myLineHeight * ratioPieceOfLineHight);
        myWhitePiece = Bitmap.createScaledBitmap(myWhitePiece, pieceWidth, pieceWidth, false);
        myBlackPiece = Bitmap.createScaledBitmap(myBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) { //判断触摸动作，ACTION_UP为单点触摸离开
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getVaLidPoint(x, y);

            if (myWhiteArray.contains(p) || myBlackArray.contains(p)) {
                return false;
            }

            if (isWhite) {
                myWhiteArray.add(p);
            } else {
                myBlackArray.add(p);
            }
            invalidate();
            isWhite = !isWhite;

        }
        return true;
    }

    //获取棋子当前的坐标
    private Point getVaLidPoint(int x, int y) {
        return new Point((int) (x / myLineHeight), (int) (y / myLineHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBroad(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    //画棋盘
    private void drawBroad(Canvas canvas) {
        int w = myPanelWidth;
        float lineHeight = myLineHeight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((i + 0.5) * lineHeight);
            canvas.drawLine(startX, y, endX, y, myPaint);        //画棋盘横向线
            canvas.drawLine(y, startX, y, endX, myPaint);        //画棋盘纵向线
        }
    }

    //画棋子
    private void drawPieces(Canvas canvas) {
        for (int i = 0; i < myWhiteArray.size(); i++) {
            Point whitePoint = myWhiteArray.get(i);
            canvas.drawBitmap(myWhitePiece, (whitePoint.x + (1 - ratioPieceOfLineHight) / 2) * myLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHight) / 2) * myLineHeight, null);
        }
        for (int i = 0; i < myBlackArray.size(); i++) {
            Point blackPoint = myBlackArray.get(i);
            canvas.drawBitmap(myBlackPiece, (blackPoint.x + (1 - ratioPieceOfLineHight) / 2) * myLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHight) / 2) * myLineHeight, null);
        }
    }

    //判断对局胜利
    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(myWhiteArray);
        boolean blackWin = checkFiveInLine(myBlackArray);

        if (whiteWin || blackWin) {
            isGameOver = true;
            if (onGameListener != null) {
                onGameListener.onGameOver(whiteWin ? WHITE_WIN : BLACK_WIN);
            }
        }

    }

    //回调一个int数据用于设置Dialog的位置
    public int getUnder() {
        return mUnder;
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;//棋子在棋盘的位置
            int y = p.y;

            //判断是否存在五子相连情况
            boolean win_flag = checkHorizontal(x, y, points) || checkVertical(x, y, points)
                    || checkLeftDiagonl(x, y, points) || checkRightDiagonl(x, y, points);
            if (win_flag) {
                return true;
            }
        }
        return false;
    }

    //判断水平五子连珠
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //往左边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        //往右边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        return false;
    }

    //判断垂直五子连珠
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //往上边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        //往下边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        return false;
    }

    //判断左斜五子连珠
    private boolean checkLeftDiagonl(int x, int y, List<Point> points) {
        int count = 1;
        //往左上边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        //往左下边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        return false;
    }

    //判断右斜五子连珠
    private boolean checkRightDiagonl(int x, int y, List<Point> points) {
        int count = 1;
        //往右上边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        //往下边数棋子
        for (int i = 1; i < MAX_PIECE_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_PIECE_LINE) return true;
        return false;
    }
    //重新开始游戏
    protected void restartGame(){
     myWhiteArray.clear();
       myBlackArray.clear();
       isGameOver = false;
        isWhite = false;
       invalidate();
         }
}