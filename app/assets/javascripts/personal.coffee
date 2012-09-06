$ = jQuery

this.show_hide = (_item) ->
	# visibility の切り替え
	if ($(_item).css("visibility") == "hidden")
		# 非表示 → 表示
		$(_item).css("visibility", "visible")
	else
		# 表示 → 非表示
		$(_item).css("visibility", "hidden")

this.show_live = ->
	if ($("#live_info").css("display") == "none")
		$("#live_info").show()
		$("#anime_list").hide()

this.show_anime = ->
	if ($("#anime_list").css("display") == "none")
		$("#anime_list").show()
		$("#live_info").hide()