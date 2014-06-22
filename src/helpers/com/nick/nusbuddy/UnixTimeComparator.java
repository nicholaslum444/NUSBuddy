package helpers.com.nick.nusbuddy;

import java.util.Comparator;

import com.nick.nusbuddy.Event;

public class UnixTimeComparator implements Comparator<Event> {
		
	@Override
	public int compare(Event e1, Event e2) {
		
		if (e1.getUnixTime() < e2.getUnixTime()) {
			return -1;
		} else if (e1.getUnixTime() == e2.getUnixTime()) {
			return 0;
		} else {
			return 1;
		}
	}
		
}