fun same_string(s1 : string, s2 : string) =
    s1 = s2

fun all_except_option (s, []) = NONE
  | all_except_option(s, x::xs) = (
        case (same_string (x, s), all_except_option(s, xs)) of
            (true, NONE) => SOME xs          
          | (true, SOME z) => SOME z
          | (false, NONE) => NONE
          | (false, SOME z) => SOME (x::z))

fun get_substitutions1 ([], s) = []
  | get_substitutions1 (x::xs, s) = (
        case all_except_option(s, x) of
            NONE => get_substitutions1(xs, s) 
          | SOME y => y @ get_substitutions1(xs, s))

fun get_substitutions2 (subs, s) = (
    let 
        fun helper([], acc) = acc
          | helper(x::xs, acc) = 
                case all_except_option(s, x) of
                    NONE => helper(xs, acc) 
                  | SOME y => helper(xs, acc @ y)
    in helper(subs, []) end)

fun similar_names(substitutions, {first=x,middle=y,last=z}) =
    let 
        fun helper([]) = []
          | helper(h::rest) = {first=h,middle=y,last=z}::helper(rest)
   in
      {first=x,middle=y,last=z}::helper(get_substitutions2(substitutions, x))
   end

datatype suit = Clubs | Diamonds | Hearts | Spades
datatype rank = Jack | Queen | King | Ace | Num of int 
type card = suit * rank

datatype color = Red | Black
datatype move = Discard of card | Draw 

exception IllegalMove

fun card_color(c) = 
    case c of
        (Spades, _) => Black
      | (Clubs, _) => Black
      | (Diamonds, _) => Red
      | (Hearts, _) => Red

fun card_value(c) = 
    case c of
        (_, Num x) => x
      | (_, Ace) => 11
      | (_, _) => 10

fun remove_card ([], c, e) = raise e
  | remove_card (x::xs, c, e) = 
        if (x = c) then xs 
        else x::remove_card(xs, c, e)

fun all_same_color([]) =  true
  | all_same_color(_::[]) = true
  | all_same_color(x::(x'::xs)) =
        (card_color(x) = card_color(x')) andalso all_same_color(x'::xs)

fun sum_cards (cs) =
    let 
        fun count ([], sum) = sum
          | count (x::xs, sum) = count(xs, sum + card_value(x)) 
    in count(cs, 0) end

fun score (held_cards, goal) =
    let 
        val sum = sum_cards(held_cards)
        val dv = if all_same_color(held_cards) then 2 else 1
   in
      if sum > goal 
      then 3 * (sum - goal) div dv
      else (goal - sum) div dv
   end

fun officiate (card_list', move_list', goal) =
    let 
        fun play (held_cards, move_list, card_list) =
            case (move_list, card_list) of
                ([], _) => score (held_cards, goal)
              | ((Draw)::rest_moves,[]) => score (held_cards, goal)
              | ((Discard c)::rest_moves, _) => 
                    play (remove_card (held_cards, c, IllegalMove), rest_moves, card_list)              
              | ((Draw)::rest_moves, new::rest_cards) => 
                    let 
                        val new_held = new::held_cards 
                    in 
                        if (sum_cards(new_held) > goal)
                        then score (new_held, goal)
                        else play (new_held, rest_moves, rest_cards)
                    end
   in play([], move_list', card_list') end