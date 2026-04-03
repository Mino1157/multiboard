package com.example.multiboard.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IBoardRepository {
	List<Board> selectArticleListByCategory(@Param("categoryId") int
			categoryId, @Param("start") int start, @Param("end") int end);
	
	Board selectArticle(int boardId);
	void updateReadCount(int boardId);
	
	int selectMaxArticleNo();
	int selectMaxFileId();
	void insertArticle(Board board);
	void insertFileData(BoardUploadFile file); 

}
