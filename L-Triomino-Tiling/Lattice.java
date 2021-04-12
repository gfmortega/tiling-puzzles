import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Stack;
public class Lattice
{
	private int s;
	private int stick_thick;

	private int level;
	private int max_level;

	Color[] colors = {
		new Color(249, 65, 68),
		new Color(248, 150, 30),
		new Color(144, 190, 109),
		new Color(87, 117, 144)
	};

	public class Cross
	{
		int x, y, s, level;
		Rectangle2D.Double H, W;
		public Cross(int x, int y, int s, int level)
		{
			this.x = x;
			this.y = y;
			this.s = s;
			this.level = level;

			H = new Rectangle2D.Double(x, y + s/2 - stick_thick/2, s, stick_thick);
			W = new Rectangle2D.Double(x + s/2 - stick_thick/2, y, stick_thick, s);
		}
		public void draw(Graphics2D g2d)
		{
			g2d.setColor(colors[level]);
			g2d.fill(H);
			g2d.fill(W);
		}
	};

	private ArrayList<Cross> sticks;
	private Stack<Cross> stack;

	private boolean visible;
	
	public void level_up()
	{
		if(!visible)
			return;
		level++;
		if(level > sticks.size())
			level = 1;
	}
	private void preprocess_sticks()
	{
		sticks = new ArrayList<Cross>();
		stack = new Stack<Cross>();

		stack.push(new Cross(0, 0, s, 0));
		while(!stack.empty())
		{
			Cross c = stack.pop();
			sticks.add(c);
			if(c.level < max_level-1)
				for(int dx = 0; dx <= 1; dx++)
					for(int dy = 0; dy <= 1; dy++)
						stack.push(new Cross(c.x + dx*(c.s>>1), c.y + dy*(c.s>>1), c.s>>1, c.level+1));
		}
	}
	public Lattice(int s, int max_level)
	{
		this.s = s;
		this.max_level = max_level;

		this.stick_thick = 6;
		this.level = 1;

		preprocess_sticks();

		this.visible = false;
	}
	public void draw(Graphics2D g2d)
	{
		if(!visible)
			return;

		for(int i = level-1; i >= 0; i--)
			sticks.get(i).draw(g2d);
	}
	public void toggleVisibility()
	{
		visible = !visible;
	}
}