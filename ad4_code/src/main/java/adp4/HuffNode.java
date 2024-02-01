package adp4;

public class HuffNode implements Comparable<HuffNode>
{
	int character;
	int frequency;

	public HuffNode(int ch, int frequency) {
		character = ch;
		this.frequency = frequency;
	}

	public int getCharacter()
	{
		return character;
	}

	public int getFrequency()
	{
		return frequency;
	}

	@Override
	public int compareTo(HuffNode node)
	{
		return Integer.compare(this.frequency, node.getFrequency());
	}
}
