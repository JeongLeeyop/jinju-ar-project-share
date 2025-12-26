package com.community.cms.api.board.service;

import com.community.cms.api.board.dto.BoardDto.ClientCommunity;
import com.community.cms.api.board.dto.BoardDto.ClientDetail;
import com.community.cms.api.board.dto.mapper.BoardMapper;
import com.community.cms.api.board.dto.search.BoardSearch;
import com.community.cms.api.board.exception.BoardNotFoundException;
import com.community.cms.api.board.repository.BoardRepository;
import com.community.cms.entity.Board;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

public interface ClientBoardService {
    ClientDetail get(String boardUid);
    ClientCommunity community(String boardUid);
}

@Service
@AllArgsConstructor
class ClientBoardServiceImpl implements ClientBoardService {
    private final BoardRepository boardRepository;

    @Override
	public ClientDetail get(String boardUid) {
		Board board = boardRepository.findById(boardUid).orElseThrow(() -> new BoardNotFoundException());
		return BoardMapper.INSTANCE.entityToClientDetail(board);
	}
    
	@Override
	public ClientCommunity community(String boardUid) {
		Board board = boardRepository.findById(boardUid).orElseThrow(() -> new BoardNotFoundException());
		return BoardMapper.INSTANCE.entityToClientCommunity(board);
	}
}
