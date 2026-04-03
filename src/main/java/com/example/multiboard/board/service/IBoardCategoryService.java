package com.example.multiboard.board.service;

import java.util.List;

import com.example.multiboard.board.model.BoardCategory;

public interface IBoardCategoryService {
	List<BoardCategory>	selectAllCategory();
	void insertNewCategory(BoardCategory boardCategory);
	void updateCategory(BoardCategory boardCategory);
	void deleteCategory(int categoryId);
}
