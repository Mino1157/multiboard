package com.example.multiboard.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface IBoardCategoryRepository {
	int selectMaxCategoryId();
	List<BoardCategory> selectAllCategory();
	void inserNewCategory(IBoardCategoryRepository boardCategory);
	void updateCategory(BoardCategory boardCategory);
	void deleteCategory(int categoryId);
}
