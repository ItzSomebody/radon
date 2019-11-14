package me.itzsomebody.radon.utils;

import java.util.List;
import java.util.ArrayList;

/**
 * @author cookiedragon234 02/Nov/2019
 */
public class StrSequence
{
	private final String[] sequence;
	
	public StrSequence(CharSequence sequence)
	{
		this(sequence.toString().toCharArray());
	}
	
	public StrSequence(char[] sequence)
	{
		
		this(new String(sequence).split(""));
	}
	
	public StrSequence(String[] sequence)
	{
		this.sequence = sequence;
	}
	
	public StrSequence(Iterable<? extends CharSequence> collection)
	{
		List<String> strList = new ArrayList<>();
		for(CharSequence charSequence : collection)
		{
			strList.add(charSequence.toString());
		}
		this.sequence = strList.toArray(new String[0]);
	}
	
	public int length()
	{
		return sequence.length;
	}
	
	public String strAt(int index)
	{
		return sequence[index];
	}
	
	public StrSequence subSequence(int start, int end)
	{
		String[] out = new String[end - start];
		System.arraycopy(sequence, start, out, 0, end - start);
		return new StrSequence(out);
	}
	
	public String[] getSequence()
	{
		return sequence;
	}
	
	@Override
	public String toString()
	{
		return String.join("", sequence);
	}
}
