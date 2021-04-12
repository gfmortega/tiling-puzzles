public class Tuple
{
	public int i, j;
	public Tuple(int i, int j)
	{
		this.i = i;
		this.j = j;
	}
	public int color()
	{
		return (i^j)&1;
	}
	public boolean adjacent(Tuple other)
	{
		return Math.abs(this.i - other.i) + Math.abs(this.j - other.j) == 1;
	}
	public boolean equals(Tuple other)
	{
		return this.i==other.i && this.j==other.j;
	}
	@Override
	public boolean equals(Object other)
	{
		return this.equals((Tuple)other);
	}
}