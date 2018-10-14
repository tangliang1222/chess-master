package cracker.example.com.chess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private ChessView mPanel;
    private AlertDialog.Builder mBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPanel = (ChessView) findViewById(R.id.main_panel);
        mBuilder  = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("结果：");

        mBuilder.setPositiveButton("再来一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPanel.restartGame();
            }
        });
        mPanel.setOnGameListener(new ChessView.onGameListener() {
            @Override
            public void onGameOver(int i) {
                String str = "";
                if (i==ChessView.WHITE_WIN){
                    str = "白棋胜利";
                }else if (i==ChessView.BLACK_WIN){
                    str = "黑棋胜利";
                }
                mBuilder.setMessage(str);
                mBuilder.setCancelable(false);//不可用返回键取消
                 AlertDialog dialog = mBuilder.create();
              Window dialogWindow = dialog.getWindow();
                 WindowManager.LayoutParams params = new WindowManager.LayoutParams();
              params.x = 0;
              params.y = mPanel.getUnder();
                dialogWindow.setAttributes(params);//设置Dialog显示的位置
           dialog.setCanceledOnTouchOutside(false);//不可点击取消
                  dialog.show();
            }
        });
    }
}
