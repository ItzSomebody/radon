package me.itzsomebody.radon.dictionaries;

import me.itzsomebody.radon.utils.RandomUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cookiedragon234 15/Nov/2019
 */
public class CreeperDictionary implements Dictionary {
    private static final String[] LYRICS = {
            "Creeper_aw_man",
            "So_we_back_in_the_mine_got_our_pickaxe_swingin_from_side_to_side_side_side_to_side",
            "This_task_a_grueling_one_hope_to_find_some_diamonds_tonight_night_night_diamonds_tonight",
            "Heads_up",
            "You_hear_a_sound_turn_around_and_look_up",
            "Total_shock_fills_your_body",
            "Oh_no_its_you_again_I_can_never_forget_those_eyes_eyes_eyes_eyes_eyes_eyes",
            "Cause_baby_tonight_the_creepers_tryin_to_steal_all_our_stuff_again",
            "Cause_baby_tonight_you_grab_your_pick_shovel_and_bolt_again_bolt_again_gain",
            "And_run_run_until_its_done_done_until_the_sun_comes_up_in_the_morn",
            "Cause_baby_tonight_the_creepers_tryin_to_steal_all_our_stuff_again_stuff_again_gain",
            "Just_when_you_think_youre_safe_overhear_some_hissing_from_right_behind_right_right_behind",
            "Thats_a_nice_life_you_have_shame_its_gotta_end_at_this_time_time_time_time_time_time_time",
            "Blows_up_then_your_health_bar_drops_and_you_could_use_a_1_up",
            "Get_inside_dont_be_tardy",
            "So_now_youre_stuck_in_there_half_a_heart_is_left_but_dont_die_die_die_die_die_die",
            "Cause_baby_tonight_the_creepers_tryin_to_steal_all_our_stuff_again",
            "Cause_baby_tonight_grab_your_pick_shovel_and_bolt_again_bolt_again_gain",
            "And_run_run_until_its_done_done_until_the_sun_comes_up_in_the_morn",
            "Cause_baby_tonight_the_creepers_tryin_to_steal_all_our_stuff_again",
            "Creepers_youre_mine_ha_ha",
            "Dig_up_diamonds_and_craft_those_diamonds_and_make_some_armor",
            "Get_it_baby_go_and_forge_that_like_you_so_MLG_pro",
            "The_swords_made_of_diamonds_so_come_at_me_bro",
            "Huh_training_in_your_room_under_the_torch_light",
            "Hone_that_form_to_get_you_ready_for_the_big_fight",
            "Every_single_day_and_the_whole_night",
            "Creepers_out_prowlin_Woo_alright",
            "Look_at_me_look_at_you",
            "Take_my_revenge_thats_what_Im_gonna_do",
            "Im_a_warrior_baby_what_else_is_new",
            "And_my_blades_gonna_tear_through_you",
            "Bring_it",
            "Cause_baby_tonight_the_creepers_tryin_to_steal_all_our_stuff_again_Get_your_Stuff",
            "Yeah_lets_take_back_the_world",
            "Yeah_baby_tonight_grab_your_sword_armor_and_go_Its_on",
            "Take_your_revenge_Woo",
            "Oh_so_fight_fight_like_its_the_last_last_night_of_your_life_life_show_them_your_bite_Woo",
            "Cause_baby_tonight_the_creepers_tryin_to_steal_our_stuff_again",
            "Cause_baby_tonight_grab_your_pick_shovel_and_bolt_again_bolt_again_gain",
            "And_run_run_until_its_done_done_until_the_sun_comes_up_in_the_morn",
            "Cause_baby_tonight",
            "Come_on_swing_your_sword_up_high",
            "The_creepers_tryin_to_steal_all_our_stuff_again",
            "Come_on_jab_your_sword_down_low"
    };

    private int index = 0;
    private int loop = 0;

    private Set<String> cache = new HashSet<>();
    private String lastGenerated;

    @Override
    public String randomString(int length) {
        StringBuilder out = new StringBuilder();
        do {
            out.append(LYRICS[RandomUtils.getRandomInt(LYRICS.length)]);
        } while (out.length() < length);

        if (out.length() > length) {
            out = new StringBuilder(out.substring(0, length));
        }

        return out.toString();
    }

    @Override
    public String uniqueRandomString(int length) {
        String s;
        do {
            s = randomString(length);
        } while (cache.contains(s));

        cache.add(s);

        return s;
    }

    @Override
    public String nextUniqueString() {
        if (index >= LYRICS.length) {
            index = 0;
        }

        String loopStr = loop + "";

        while (loopStr.length() < 4)
            loopStr = "0" + loopStr;

        lastGenerated = loopStr + "_" + LYRICS[index];
        index++;
        loop++;
        return lastGenerated;
    }

    @Override
    public String lastUniqueString() {
        return lastGenerated;
    }

    @Override
    public String getDictionaryName() {
        return "creeper";
    }

    @Override
    public void reset() {
        index = 0;
        loop = 0;
    }

    @Override
    public Dictionary copy() {
        return new CreeperDictionary();
    }
}