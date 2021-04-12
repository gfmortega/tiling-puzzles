import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
public class Cycle
{
	// hard coded Cycle for the 8x8 case
	private int tileSize;
	private int offset;
	private int r_offset;
	private int circle_diam;
	private int stick_thick;
	private int stick_len;

	private int mode;
	private ArrayList<Tuple> tainted;

	private Tuple sequence[];

	Cycle(int tileSize, ArrayList<Tuple> tainted)
	{
		this.tileSize = tileSize;
		this.circle_diam = tileSize/4;
		this.offset = (tileSize-circle_diam)/2;
		this.stick_thick = circle_diam*3/10;
		this.r_offset = (tileSize-stick_thick)/2;
		this.stick_len = 2*(tileSize-this.r_offset);
		
		this.mode = 0;
		this.tainted = tainted;

		sequence = new Tuple[65];
		int t = 0;
		for(int j = 7; j >= 0; j--)
			sequence[t++] = new Tuple(0,j);

		for(int j = 0; j < 8; j += 2)
		{
			for(int i = 1; i < 8; i++)
				sequence[t++] = new Tuple(i, j);
			for(int i = 7; i >= 1; i--)
				sequence[t++] = new Tuple(i, j+1);
		}

		sequence[64] = sequence[0];
	}
	public void draw(Graphics2D g2d)
	{
		if(mode==0)
			return;

		int col = 0;
		Color[] paths = {new Color(255,50,50), new Color(50,50,255)};

		for(int t = 0; t < 64; t++)
		{
			int i = 0, j = 0;
			int width = this.stick_thick, height = this.stick_thick;
			if(sequence[t].i == sequence[t+1].i)
			{
				i = sequence[t].i;
				j = Math.min(sequence[t].j, sequence[t+1].j);
				height = this.stick_len;
			}
			else
			{
				i = Math.min(sequence[t].i, sequence[t+1].i);
				j = sequence[t].j;
				width = this.stick_len;
			}

			Rectangle2D.Double r = new Rectangle2D.Double(i*tileSize+r_offset, j*tileSize+r_offset, width, height);
			if(mode==1)
			{
				g2d.setColor(Color.YELLOW);
				g2d.fill(r);
				g2d.setColor(Color.BLACK);
				g2d.draw(r);
			}
			else
			{
				if(!tainted.contains(sequence[t+1]))
				{
					if(!tainted.contains(sequence[t]))
					{
						g2d.setColor(paths[col]);
						g2d.fill(r);
						g2d.setColor(Color.BLACK);
						g2d.draw(r);
					}
					else
						col ^= 1;
				}
			}
		}

		col = 0;
		for(int t = 0; t < 64; t++)
		{
			int i = sequence[t].i, j = sequence[t].j;
			Ellipse2D.Double e = new Ellipse2D.Double(i*tileSize+offset, j*tileSize+offset, circle_diam, circle_diam);
			if(mode==1)
			{
				g2d.setColor(Color.YELLOW);
				g2d.fill(e);
				g2d.setColor(Color.BLACK);
				g2d.draw(e);
			}
			else
			{
				if(!tainted.contains(sequence[t]))
				{
					g2d.setColor(paths[col]);
					g2d.fill(e);
					g2d.setColor(Color.BLACK);
					g2d.draw(e);
				}
				else
					col ^= 1;
			}
		}
	}
	public void toggleVisible()
	{
		mode = (mode+1)%3;
		if(mode==2 && tainted.size() != 2)
			mode = (mode+1)%3;
	}
}