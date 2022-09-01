fun is_older (a: int * int * int, b: int * int * int) = 
  (#1 a < #1 b) 
  orelse ((#1 a = #1 b) andalso (#2 a < #2 b)) 
  orelse ((#1 a = #1 b) andalso (#2 a = #2 b) andalso (#3 a < #3 b));

fun number_in_month(l: (int * int * int) list, month: int) =
  let
    fun internal(li: (int * int * int) list) =  
      if null li then 0
      else (if #2 (hd li) = month then 1 else 0) + internal(tl li);
  in 
    internal(l)
  end

fun number_in_months(dates : (int * int * int) list, months : int list) =
    if null months then 0
    else number_in_month(dates, hd months) + number_in_months(dates, tl months)

fun dates_in_month(l: (int * int * int) list, month: int) =
  let
    fun internal(dates: (int * int * int) list) =
      if null dates then []
      else (
        if #2 (hd dates) = month 
        then (hd dates) :: internal(tl dates) 
        else internal(tl dates)
      )
  in
    internal(l)
  end

fun dates_in_months(dates : (int * int * int) list, months : int list) =
  if null months then []
  else dates_in_month(dates, hd months) @ dates_in_months(dates, tl months)

fun get_nth(l: 'a list, c: int) =
  if c = 1 then hd l
  else get_nth(tl l, c-1)

fun date_to_string (date: int * int * int) =
  let
    val MONTHS = [
      "January", "February", "March", "April", "May", "June", "July",
      "August", "September", "October", "November", "December"
    ]
  in
    get_nth(MONTHS, #2 date) ^ " " ^ Int.toString(#3 date) ^ ", " ^ Int.toString(#1 date)
  end

fun number_before_reaching_sum(sum: int, l: int list) =
  if hd(l) >= sum then 0
  else number_before_reaching_sum(sum - (hd l), tl l) + 1

fun what_month(day_of_year: int) =
  let
    val DAYS_EACH_MONTH = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
  in
    number_before_reaching_sum(day_of_year, DAYS_EACH_MONTH) + 1
  end

fun month_range(d1: int, d2: int)=
  if d1 > d2 then []
  else what_month(d1) :: month_range(d1 + 1, d2)

fun oldest(dates: (int * int * int) list) =
  if null dates then NONE
  else
    let
      val tl_oldest = oldest(tl dates)
      val tl_has_older = isSome tl_oldest andalso is_older(valOf tl_oldest, hd dates)
    in
      if tl_has_older then tl_oldest
      else SOME(hd dates)
    end