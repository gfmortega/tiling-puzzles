import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
public class Tile
{
	public boolean isFilled;
	private boolean isHighlighted;
	private boolean isChess;
	private Color chessColor;

	private int colorID;
	private int size;
	private int x, y;
	public Tile(int x, int y)
	{
		this.x = (x-2)*GameData.tileSize;
		this.y = (y-2)*GameData.tileSize;
		isFilled = false;
		isHighlighted = false;
		colorID = 0;

		isChess = false;
		// Pale black vs Dirty white
		chessColor = ((x^y)&1) > 0 ? new Color(20,20,20) : new Color(232,228,201);
	}
	public void draw(Graphics2D g2d)
	{
		Color useColor;
		if(isHighlighted)
		{
			if(isFilled)
				useColor = GameData.invalidHighlight;
			else
				useColor = GameData.validHighlight;
		}
		else
		{
			if(isFilled)
				useColor = GameData.colors[colorID];
			else if(isChess)
				useColor = chessColor;
			else
				useColor = GameData.backgroundColor;
		}
		g2d.setColor(useColor);
		Rectangle2D.Double r = new Rectangle2D.Double(x,y,GameData.tileSize,GameData.tileSize);
		g2d.fill(r);
		g2d.setColor(GameData.borderColor);
		g2d.draw(r);
	}
	public void taint()
	{
		isFilled = true;
		colorID = 0;
	}
	public void fillIn(int ID)
	{
		isFilled = true;
		colorID = ID;
	}
	public void unfill()
	{
		isFilled = false;
		colorID = 0;
	}
	public void toggleHighlight()
	{
		isHighlighted = !isHighlighted;
	}
	public void chessToggle()
	{
		isChess = !isChess;
	}
	public int getID()
	{
		return colorID;
	}
}