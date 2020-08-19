package dev.wirlie.bungeecord.glist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TemporalPaginator<T> {
	private List<T> data;
	private int totalPages;
	private final int pageSize;
	private final long createdAt = System.currentTimeMillis();
	private boolean testedData = false;

	public TemporalPaginator(Collection<T> data, int pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		}

		this.data = new ArrayList<>(data);
		this.pageSize = pageSize;
		this.totalPages = (int)Math.ceil((double)data.size() / (double)pageSize);
	}

	@SuppressWarnings("unused")
	@SafeVarargs
	public final void testData(T... data) {
		if (!this.testedData) {
			this.data.addAll(Arrays.asList(data));
			this.totalPages = (int)Math.ceil((double)this.data.size() / (double)this.pageSize);
			this.testedData = true;
		}
	}

	public List<T> getPage(int page) {
		if (this.totalPages != 0 && page >= 1 && page <= this.totalPages) {
			int index = page - 1;
			return this.data.stream().skip(index * this.pageSize).limit(this.pageSize).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}

	public List<T> getFullData() {
		return this.data;
	}

	private long getCreatedAt() {
		return this.createdAt;
	}

	public boolean shouldUpdate(long millisToKeep) {
		return System.currentTimeMillis() - this.getCreatedAt() >= millisToKeep;
	}

	public void update(Collection<T> data) {
		this.data = new ArrayList<>(data);
		this.totalPages = (int)Math.ceil((double)data.size() / (double)this.pageSize);
	}

	public int getTotalPages() {
		return this.totalPages;
	}

	public int dataSize() {
		return this.data.size();
	}
}
