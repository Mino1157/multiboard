package com.example.multiboard.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.multiboard.board.model.BoardCategory;

@Mapper
@Repository
public interface IBoardCategoryRepository {
	int selectMaxCategoryId();
	List<BoardCategory> selectAllCategory();
	void insertNewCategory(BoardCategory boardCategory);
	void updateCategory(BoardCategory boardCategory);
	void deleteCategory(int categoryId);
}
