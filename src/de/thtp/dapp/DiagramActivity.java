package de.thtp.dapp;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.ResultList;
import de.thtp.dapp.app.Session;

class DiagramView extends View {
	private static final int[] PLAYER_COLORS = new int[] { Color.RED, Color.CYAN,
			Color.BLUE, Color.GREEN, Color.MAGENTA, Color.GRAY, Color.YELLOW };
	private int lowestLinePoints;
	private int highestLinePoints;
	private int diaHeight;
	private int width;
	private int pointsDiff;
	private double sizeF;

	public DiagramView(Context context) {
		super(context);

	}

	private void initDraw() {
		pointsDiff = highestLinePoints - lowestLinePoints;
		diaHeight = getHeight() - 34;
		width = getWidth();
		sizeF = diaHeight / (double) (pointsDiff == -1 ? 1 : pointsDiff + 1);
	}

	private int vpos(int points) {
		return (int) (diaHeight - (points - lowestLinePoints) * sizeF) + 20;
	}

	private void drawPointLine(Canvas canvas, int points) {
		int h = vpos(points);
		Paint paint = new Paint();
		paint.setColor(Color.LTGRAY);
		canvas.drawLine(0, h, width, h, paint);
		paint.setColor(Color.BLACK);
		canvas.drawText("" + points, 10, h + 10, paint);
	}

	protected void onDraw(Canvas canvas) {
		ResultList rl = Session.getResultList();
		int[][] table = rl.valuesTable();
		int[] peaks = rl.resultPeaks();
		int minPoints = peaks[0];
		int maxPoints = peaks[1];
		lowestLinePoints = ((minPoints) / 10) * 10;
		highestLinePoints = ((maxPoints) / 10) * 10;
		if (lowestLinePoints == 0) {
			lowestLinePoints = peaks[0];
		}
		if (highestLinePoints == 0) {
			highestLinePoints = peaks[1];
		}
		initDraw();
		int step = pointsDiff;
		while (step / 5 > 5) {
			step /= 2;
			if (step <= 1) {
				break;
			}
		}
		for (int p = lowestLinePoints; p < highestLinePoints; p += step) {
			drawPointLine(canvas, p);
		}
		int leftPos = 8;
		int cnt = 0;
		Paint paint = new Paint();
		Map<Player, Integer> playerColors = new HashMap<Player, Integer>();
		int gameWidth = width / table.length;
		//for (Player p : Session.getSessionPlayers()) {
		for (Player p : Session.getVisibleSessionPlayers()) {

			playerColors.put(p, PLAYER_COLORS[cnt]);
			paint.setColor(playerColors.get(p));
			String txt = p.name;
			canvas.drawText(txt, leftPos, 16, paint);
			Rect b = new Rect();
			paint.getTextBounds(txt, 0, txt.length(), b);
			leftPos += b.width() + 8;

			int oldPoints = 0;
			for (int gamePos = 0; gamePos < table.length; gamePos++) {
				int points = table[gamePos][cnt];
				int vold = vpos(oldPoints);
				int vp = vpos(points);
				canvas.drawLine(gamePos * gameWidth, vold + cnt, (gamePos + 1)
						* gameWidth, vp + cnt, paint);
				oldPoints = points;
			}
			cnt++;

		}
	}

}

public class DiagramActivity extends DappActivity {
	private DiagramView dView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dView = new DiagramView(this);
		setContentView(dView);
	}
}
