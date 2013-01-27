/**   
 *
 * Copyright (C) 2013 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.util;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import javax.naming.NamingException;

import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.logreport.ErrorParserLogger;

public class ThreadUtils {

	public static class ThreadGroupFactory implements ThreadFactory {

		private final ThreadGroup group;

		public ThreadGroupFactory(ThreadGroup group) {
			this.group = group;
		}

		@Override
		public Thread newThread(Runnable target) {
			return new Thread(group, target);
		}

	}

	public static class ThreadInfo {

		private final String name;

		private final String location;

		private final State state;

		private final String fullStackTrace;

		public ThreadInfo(Thread thread) {
			this.name = thread.getName();
			StackTraceElement[] elements = thread.getStackTrace();
			String l = ErrorParserLogger.getLocation(elements);
			if (l == null)
				l = ErrorParserLogger.getFirstLocation(elements);
			this.fullStackTrace = ErrorParserLogger.getFullStackTrace(elements);
			this.location = l;
			this.state = thread.getState();
		}

		public String getName() {
			return name;
		}

		public String getLocation() {
			return location;
		}

		public State getState() {
			return state;
		}

		public String getFullStackTrace() {
			return fullStackTrace;
		}
	}

	public static Thread[] getThreadArray(ThreadGroup group) {
		Thread[] threads = new Thread[group.activeCount()];
		for (;;) {
			int l = group.enumerate(threads);
			if (l == threads.length)
				break;
			threads = new Thread[l];
		}
		return threads;
	}

	public static List<ThreadInfo> getInfos(ThreadGroup... groups)
			throws SearchLibException, NamingException {
		if (groups == null)
			return null;
		int count = 0;
		List<Thread[]> threadsArrayList = new ArrayList<Thread[]>(groups.length);
		for (ThreadGroup group : groups) {
			Thread[] threadArray = ThreadUtils.getThreadArray(group);
			threadsArrayList.add(threadArray);
			count += threadArray.length;
		}

		List<ThreadInfo> threadList = new ArrayList<ThreadInfo>(count);
		for (Thread[] threadArray : threadsArrayList)
			for (Thread thread : threadArray)
				threadList.add(new ThreadInfo(thread));
		return threadList;
	}

}
