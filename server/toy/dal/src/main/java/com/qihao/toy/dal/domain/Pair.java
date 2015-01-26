package com.qihao.toy.dal.domain;

import lombok.Data;

@Data
public class Pair<FIRST, SECOND> {

	private final FIRST first;
	private final SECOND second;

	public Pair(FIRST first, SECOND second) {
		this.first = first;
		this.second = second;
	}

}
