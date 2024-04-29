<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<div class="listpage-wrap">
	<ul class="category clear">
		<li class="active" code="541">전체</li>
		<li code="591">신선/가공식품</li>
		<li code="592">홍삼/건강식품</li>
		<li code="593">생활용품</li>
		<li code="594">화장품/뷰티</li>
		<li code="595">의류/잡화</li>
		<li code="596">스포츠패션/의류/신발</li>
		<li code="597">장병용품</li>
		<li code="598">전통주</li>
	</ul>
	<div class="filter clear">
		<div class="btn-sch-detail"
			onclick="javascript:$('.sch-detail').toggleClass('hide');">
			<i class="icon-filter"></i> <span>상세검색</span>
		</div>
		<ul class="sorting">
			<li class="active">신상품순</li>
			<li>인기순</li>
			<li>낮은 가격순</li>
			<li>높은 가격순</li>
		</ul>
	</div>	
	
</div>



<div id="itemBox" class="itemBox"></div>