package com.example.getmyenglishname

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.getmyenglishname.api.BaiduTranslateClient
import com.example.getmyenglishname.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var baiduTranslateClient: BaiduTranslateClient

    companion object {
        private const val DEFAULT_APP_ID = "YOUR_APP_ID"
        private const val DEFAULT_APP_SECRET = "YOUR_APP_SECRET"
    }

    private fun convertToPinyin(chinese: String): String {
        val format = HanyuPinyinOutputFormat().apply {
            caseType = HanyuPinyinCaseType.LOWERCASE
            toneType = HanyuPinyinToneType.WITHOUT_TONE
            vCharType = HanyuPinyinVCharType.WITH_V
        }

        return chinese.map { char ->
            PinyinHelper.toHanyuPinyinStringArray(char, format)?.firstOrNull()
                ?: char.toString()
        }.joinToString(" ")
    }

    // 声母映射
    private val initialConsonants = mapOf(
        "zh" to "ch",
        "ch" to "ch'",
        "sh" to "sh",
        "b" to "p",
        "p" to "p'",
        "d" to "t",
        "t" to "t'",
        "g" to "k",
        "k" to "k'",
        "z" to "ts",
        "c" to "ts'",
        "j" to "ch",
        "q" to "ch'",
        "x" to "hs",
        "r" to "j",
        "f" to "f",
        "h" to "h",
        "m" to "m",
        "n" to "n",
        "l" to "l",
        "s" to "s"
    )

    // 韵母映射
    private val vowels = mapOf(
        "iong" to "iung",
        "iang" to "iang",
        "uang" to "uang",
        "ying" to "ying",
        "yuan" to "yüan",
        "ing" to "ing",
        "ang" to "ang",
        "eng" to "eng",
        "ong" to "ung",
        "iu" to "iu",
        "ui" to "uei",
        "un" to "un",
        "ue" to "üeh",
        "üe" to "üeh",
        "ve" to "üeh",
        "ie" to "ieh",
        "ia" to "ia",
        "ua" to "ua",
        "uo" to "o",
        "ai" to "ai",
        "ei" to "ei",
        "ao" to "ao",
        "ou" to "ou",
        "an" to "an",
        "en" to "en",
        "in" to "in",
        "ian" to "ien",
        "uan" to "uan",
        "üan" to "üan",
        "van" to "üan",
        "er" to "erh",
        "yi" to "i",
        "wu" to "u",
        "yu" to "yü",
        "yue" to "yüeh",
        "yin" to "yin",
        "yun" to "yün",
        "u" to "u",
        "i" to "i",
        "e" to "e",
        "o" to "o",
        "a" to "a",
        "ü" to "ü",
        "v" to "ü"
    )

    // 特殊情况处理规则
    private val specialCases = mapOf(
        "ju" to "chü",
        "qu" to "ch'ü",
        "xu" to "hsü",
        "yi" to "i",
        "wu" to "wu",
        "ye" to "yeh",
        "yue" to "yüeh",
        "juan" to "chüan",
        "quan" to "ch'üan",
        "xuan" to "hsüan",
        "yuan" to "yüan"
    )

    // 扩展函数：判断字符是否为元音
    private fun Char.isVowel(): Boolean {
        return this in "aeiouüv"
    }

    // 威妥玛拼音与英文名对应关系（姓氏）
    private val surnamePinyinMap = mapOf(
        "zhang" to listOf("Jazz", "Jade", "Jupiter", "Justice", "Journey", "Jungle", "Jasper", "Jubilee", "Jingle", "Jumper", "Jester", "Jinx", "Joker"),
        "wang" to listOf("Wing", "Wonder", "Whisper", "Wander", "Wizard", "Warden", "Weaver", "Warrior", "Winter", "Walker", "Watcher", "Wisher", "Wanderer"),
        "li" to listOf("Leaf", "Light", "Legend", "Lyric", "Lotus", "Lunar", "Lark", "Luminous", "Lens", "Laser", "Lightning", "Lantern", "Lucent"),
        "zhao" to listOf("Jazz", "Journey", "Jewel", "Jester", "Jumper", "Joker", "Jinx", "Jingle", "Justice", "Jupiter", "Jasper", "Jubilee", "Jade"),
        "chen" to listOf("Charm", "Chime", "Cherry", "Cheer", "Chase", "Chance", "Chief", "Chorus", "Cipher", "Chrome", "Crystal", "Crimson", "Crown"),
        "liu" to listOf("Leaf", "Lyric", "Lunar", "Lark", "Lotus", "Lucent", "Legend", "Light", "Luminous", "Lantern", "Lightning", "Luster", "Laser"),
        "yang" to listOf("Young", "Yonder", "Yearning", "Yielding", "Youthful", "Yawning", "Yelling", "Yachting", "Yielder", "Younger", "Yawner", "Yodeler", "Yester"),
        "huang" to listOf("Wing", "Wander", "Wonder", "Whisper", "Watcher", "Warden", "Weaver", "Warrior", "Wisher", "Walker", "Winger", "Whisperer", "Wonderer"),
        "wu" to listOf("Wood", "Wind", "Wave", "Wish", "Wise", "World", "Whale", "Wander", "Wonder", "Whisper", "Watcher", "Warden", "Weaver"),
        "zhou" to listOf("Joy", "Journey", "Jumper", "Jester", "Joker", "Jinx", "Jingle", "Justice", "Jupiter", "Jasper", "Jubilee", "Jade", "Jazz"),
        "xu" to listOf("Shine", "Shield", "Shadow", "Shimmer", "Shore", "Shower", "Shape", "Shade", "Shell", "Sharp", "Sheer", "Shiner", "Shifter"),
        "sun" to listOf("Star", "Shine", "Storm", "Stream", "Spark", "Spring", "Song", "Sound", "Spirit", "Space", "Sphere", "Sprout", "Sprinter"),
        "ma" to listOf("Mark", "Maker", "Master", "Marvel", "Mariner", "Mender", "Minder", "Mover", "Mixer", "Mentor", "Merger", "Molder", "Muster"),
        "zhu" to listOf("Dream", "Drift", "Drake", "Drape", "Drawer", "Drifter", "Dreamer", "Drafter", "Driver", "Drummer", "Drinker", "Dancer", "Diver"),
        "he" to listOf("Heart", "Hope", "Honor", "Haven", "Harbor", "Helper", "Healer", "Hunter", "Holder", "Heeder", "Herder", "Hopper", "Hinder"),
        "gao" to listOf("Gold", "Grace", "Grant", "Giver", "Gazer", "Glider", "Grower", "Gather", "Glancer", "Gleamer", "Glimmer", "Grazer", "Grinder"),
        "lin" to listOf("Light", "Lyric", "Lunar", "Lark", "Lotus", "Lucent", "Legend", "Luminous", "Lantern", "Lightning", "Luster", "Laser", "Lens"),
        "luo" to listOf("Lord", "Light", "Lunar", "Lark", "Lotus", "Lucent", "Legend", "Luminous", "Lantern", "Lightning", "Luster", "Laser", "Lens"),
        "zheng" to listOf("Journey", "Justice", "Jester", "Jumper", "Joker", "Jinx", "Jingle", "Jupiter", "Jasper", "Jubilee", "Jade", "Jazz", "Jester"),
        "liang" to listOf("Light", "Lyric", "Lunar", "Lark", "Lotus", "Lucent", "Legend", "Luminous", "Lantern", "Lightning", "Luster", "Laser", "Lens"),
        "xie" to listOf("Share", "Shine", "Shield", "Shadow", "Shimmer", "Shore", "Shower", "Shape", "Shade", "Shell", "Sharp", "Sheer", "Shiner"),
        "tang" to listOf("Thunder", "Thinker", "Tracker", "Trader", "Trainer", "Treader", "Trapper", "Trimmer", "Trigger", "Trekker", "Tracer", "Turner", "Tamer"),
        "cao" to listOf("Crown", "Crystal", "Crimson", "Crafter", "Cruiser", "Crawler", "Creeper", "Catcher", "Caller", "Caster", "Carver", "Climber", "Closer"),
        "feng" to listOf("Fire", "Flash", "Flame", "Flyer", "Finder", "Fighter", "Filler", "Folder", "Former", "Fader", "Feeder", "Fencer", "Floater"),
        "cheng" to listOf("Chase", "Chance", "Cheer", "Chime", "Charm", "Chief", "Chorus", "Chanter", "Chooser", "Changer", "Charger", "Checker", "Chaser")
    )

    // 威妥玛拼音与英文名对应关系（名字）
    private val givenNamePinyinMap = mapOf(
        "wei" to listOf("Wave", "Wind", "Wing", "Wish", "Wise", "Whale", "Wander", "Wonder", "Whisper", "Watcher", "Warden", "Weaver", "Warrior"),
        "ming" to listOf("Moon", "Mind", "Might", "Mirth", "Mist", "Mint", "Miner", "Mirror", "Mirage", "Miracle", "Mission", "Mystic", "Mingle"),
        "hong" to listOf("Heart", "Hope", "Honor", "Haven", "Harbor", "Helper", "Healer", "Hunter", "Holder", "Heeder", "Herder", "Hopper", "Hinder"),
        "jing" to listOf("Joy", "Jump", "Jazz", "Jade", "Jest", "Join", "Juice", "Jewel", "Jingle", "Justice", "Jupiter", "Jasper", "Jubilee"),
        "hui" to listOf("Wave", "Wind", "Wing", "Wish", "Wise", "Whale", "Wander", "Wonder", "Whisper", "Watcher", "Warden", "Weaver", "Warrior"),
        "jun" to listOf("Joy", "Jump", "Jazz", "Jade", "Jest", "Join", "Juice", "Jewel", "Jingle", "Justice", "Jupiter", "Jasper", "Jubilee"),
        "xiang" to listOf("Song", "Shine", "Star", "Spring", "Stream", "Storm", "Spark", "Spirit", "Space", "Sphere", "Sprout", "Sprinter", "Spinner"),
        "yu" to listOf("Youth", "Yield", "Yonder", "Yearning", "Yielding", "Youthful", "Yawning", "Yelling", "Yachting", "Yielder", "Younger", "Yawner", "Yester"),
        "hao" to listOf("Heart", "Hope", "Honor", "Haven", "Harbor", "Helper", "Healer", "Hunter", "Holder", "Heeder", "Herder", "Hopper", "Hinder"),
        "feng" to listOf("Fire", "Flash", "Flame", "Flyer", "Finder", "Fighter", "Filler", "Folder", "Former", "Fader", "Feeder", "Fencer", "Floater"),
        "ying" to listOf("Ring", "Wing", "Spring", "Swing", "String", "Sting", "Sing", "Bring", "Fling", "Cling", "Thing", "King", "Ming"),
        "yan" to listOf("Young", "Yield", "Yonder", "Yearning", "Yielding", "Youthful", "Yawning", "Yelling", "Yachting", "Yielder", "Younger", "Yawner", "Yester"),
        "min" to listOf("Moon", "Mind", "Might", "Mirth", "Mist", "Mint", "Miner", "Mirror", "Mirage", "Miracle", "Mission", "Mystic", "Mingle"),
        "lan" to listOf("Land", "Lane", "Lake", "Lace", "Lamp", "Lance", "Lark", "Laser", "Latch", "Layer", "Leader", "Leaper", "Learner"),
        "hua" to listOf("Heart", "Hope", "Honor", "Haven", "Harbor", "Helper", "Healer", "Hunter", "Holder", "Heeder", "Herder", "Hopper", "Hinder"),
        "ping" to listOf("Ring", "Wing", "Spring", "Swing", "String", "Sting", "Sing", "Bring", "Fling", "Cling", "Thing", "King", "Ming"),
        "xin" to listOf("Scene", "Seen", "Shine", "Sign", "Sine", "Spin", "Skin", "Twin", "Win", "Pin", "Fin", "Tin", "Din"),
        "yong" to listOf("Young", "Yield", "Yonder", "Yearning", "Yielding", "Youthful", "Yawning", "Yelling", "Yachting", "Yielder", "Younger", "Yawner", "Yester"),
        "gang" to listOf("Ring", "Wing", "Spring", "Swing", "String", "Sting", "Sing", "Bring", "Fling", "Cling", "Thing", "King", "Ming"),
        "li" to listOf("Leaf", "Light", "Legend", "Lyric", "Lotus", "Lunar", "Lark", "Luminous", "Lens", "Laser", "Lightning", "Lantern", "Lucent"),
        "na" to listOf("Night", "Near", "Note", "Noble", "Nurse", "Nature", "Nectar", "Nester", "Namer", "Nailer", "Needer", "Netter", "Nodder"),
        "xiong" to listOf("Song", "Strong", "Spring", "String", "Swing", "Sting", "Sing", "Bring", "Fling", "Cling", "Thing", "King", "Ming"),
        "tao" to listOf("Tower", "Toner", "Taker", "Timer", "Tamer", "Trader", "Tracer", "Tracker", "Trainer", "Treader", "Trapper", "Trimmer", "Trigger"),
        "jian" to listOf("Jean", "Jane", "June", "Joan", "Jade", "Join", "Joint", "Joiner", "Jumper", "Jester", "Jinxer", "Juicer", "Judger"),
        "wen" to listOf("When", "Win", "Wine", "Wing", "Wind", "Wire", "Wise", "Wish", "Winter", "Winner", "Winder", "Wiper", "Wider"),
        "lin" to listOf("Leaf", "Light", "Legend", "Lyric", "Lotus", "Lunar", "Lark", "Luminous", "Lens", "Laser", "Lightning", "Lantern", "Lucent"),
        "fang" to listOf("Ring", "Wing", "Spring", "Swing", "String", "Sting", "Sing", "Bring", "Fling", "Cling", "Thing", "King", "Ming"),
        "ting" to listOf("Thing", "Ring", "Wing", "Spring", "Swing", "String", "Sting", "Sing", "Bring", "Fling", "Cling", "King", "Ming"),
        "zhen" to listOf("Jane", "Jean", "June", "Joan", "Jade", "Join", "Joint", "Joiner", "Jumper", "Jester", "Jinxer", "Juicer", "Judger"),
        "mei" to listOf("May", "Make", "Mate", "Maze", "Main", "Mail", "Maker", "Mater", "Mazer", "Mainer", "Mailer", "Marker", "Marcher")
    )

    // 单字中文与英文对应关系
    private val singleCharacterMap = mapOf(
        "明" to listOf("Bright", "Clear", "Light", "Shine", "Brilliant", "Radiant", "Luminous", "Glow", "Dawn", "Morning", "Vision", "Clarity", "Lucid"),
        "华" to listOf("Splendid", "Glory", "Grand", "Noble", "Majestic", "Royal", "Elite", "Prime", "Flower", "Blessed", "Grace", "Honor", "Precious"),
        "伟" to listOf("Great", "Mighty", "Grand", "Power", "Strong", "Noble", "Majestic", "Vast", "Giant", "Immense", "Supreme", "Superb", "Magnificent"),
        "强" to listOf("Strong", "Mighty", "Power", "Force", "Vigor", "Robust", "Powerful", "Strength", "Solid", "Firm", "Bold", "Brave", "Dynamic"),
        "勇" to listOf("Brave", "Valor", "Bold", "Hero", "Courage", "Fearless", "Gallant", "Valiant", "Daring", "Heroic", "Intrepid", "Dauntless", "Fierce"),
        "军" to listOf("Army", "Force", "Power", "Valor", "Might", "Warrior", "Guard", "Shield", "Defend", "Protect", "Legion", "Knight", "Soldier"),
        "建" to listOf("Build", "Create", "Found", "Forge", "Shape", "Form", "Craft", "Design", "Make", "Raise", "Erect", "Frame", "Structure"),
        "文" to listOf("Culture", "Letter", "Civil", "Grace", "Scholar", "Learn", "Write", "Gentle", "Literate", "Refined", "Elegant", "Classic", "Literary"),
        "平" to listOf("Peace", "Level", "Calm", "Even", "Steady", "Balance", "Stable", "Gentle", "Smooth", "Serene", "Tranquil", "Quiet", "Still"),
        "志" to listOf("Will", "Aspire", "Ambition", "Goal", "Resolve", "Purpose", "Intent", "Dream", "Vision", "Spirit", "Mind", "Soul", "Heart")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化百度翻译客户端
        baiduTranslateClient = BaiduTranslateClient(
            appId = DEFAULT_APP_ID,  // 替换为您的百度翻译 APP ID
            appSecret = DEFAULT_APP_SECRET  // 替换为您的百度翻译密钥
        )

        setupUI()
    }

    private fun setupUI() {
        binding.btnGetName.setOnClickListener {
            val chineseName = binding.etChineseName.text.toString()
            if (chineseName.isNotEmpty()) {
                try {
                    // 获取拼音
                    val pinyinName = convertToPinyin(chineseName)
                    binding.tvPinyinName.text = "拼音：$pinyinName"

                    // 获取威妥玛拼音
                    val wadeGilesName = convertToWadeGiles(pinyinName)
                    binding.tvWadeGilesName.text = "威妥玛拼音：$wadeGilesName"

                    // 使用本地映射获取英文名
                    val englishName = getEnglishName(chineseName)
                    binding.tvEnglishName.text = "英文名：$englishName"
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "转换失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "请输入中文名", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnOnlineQuery.setOnClickListener {
            val chineseName = binding.etChineseName.text.toString()
            if (chineseName.isEmpty()) {
                Toast.makeText(this, "请输入中文名", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 检查 API KEY 是否已更新
            if (baiduTranslateClient.appId == DEFAULT_APP_ID || 
                baiduTranslateClient.appSecret == DEFAULT_APP_SECRET) {
                Toast.makeText(this, "请更换你的API_KEY", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // 使用百度翻译 API 获取英文名
                    val englishName = baiduTranslateClient.translateToEnglish(chineseName)
                    binding.tvEnglishName.text = "英文名（在线翻译）：$englishName"
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "在线翻译失败：${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getEnglishName(chineseName: String): String {
        try {
            // 创建拼音输出格式
            val format = HanyuPinyinOutputFormat().apply {
                caseType = HanyuPinyinCaseType.LOWERCASE
                toneType = HanyuPinyinToneType.WITHOUT_TONE
                vCharType = HanyuPinyinVCharType.WITH_V
            }

            // 分离姓和名
            val surname = chineseName[0]
            val givenName = chineseName.substring(1)

            // 转换姓氏
            val surnamePinyin = PinyinHelper.toHanyuPinyinStringArray(surname, format)?.firstOrNull()
                ?: return "无法转换该汉字"
            
            // 获取姓氏的威妥玛拼音
            val surnameWadeGiles = convertToWadeGiles(surnamePinyin)
            
            // 获取姓氏对应的英文名（从拼音映射表中）
            val englishSurnameFromPinyin = surnamePinyinMap[surnameWadeGiles.toLowerCase()]?.random()
                ?: surnameWadeGiles.capitalize()

            // 转换名字中的每个字
            val givenNameResults = givenName.map { char ->
                val pinyin = PinyinHelper.toHanyuPinyinStringArray(char, format)?.firstOrNull()
                    ?: return "无法转换该汉字"
                
                // 获取威妥玛拼音
                val wadeGiles = convertToWadeGiles(pinyin)
                
                // 从拼音映射表获取英文名
                val englishFromPinyin = givenNamePinyinMap[wadeGiles.toLowerCase()]?.random()
                
                // 从单字映射表获取英文名
                val englishFromChar = singleCharacterMap[char.toString()]?.random()
                
                // 选择更好的翻译结果
                val selectedEnglish = when {
                    englishFromPinyin == null && englishFromChar == null -> wadeGiles.capitalize()
                    englishFromPinyin == null -> englishFromChar!!
                    englishFromChar == null -> englishFromPinyin
                    else -> {
                        // 根据一些规则选择更好的名字
                        // 1. 优先选择较短的词（3-6个字母）
                        // 2. 避免重复的词
                        // 3. 确保词的含义积极正面
                        if (englishFromChar.length in 3..6 && 
                            englishFromChar !in listOf(englishSurnameFromPinyin)) {
                            englishFromChar
                        } else {
                            englishFromPinyin
                        }
                    }
                }
                
                Triple(selectedEnglish, wadeGiles, char.toString())
            }

            // 组合完整的英文名
            val englishGivenNames = givenNameResults.map { it.first }
            val wadeGilesNames = givenNameResults.map { it.second }
            val originalChars = givenNameResults.map { it.third }

            // 构建结果字符串
            val fullEnglishName = "$englishSurnameFromPinyin ${englishGivenNames.joinToString(" ")}"
            val fullWadeGiles = "$surnameWadeGiles ${wadeGilesNames.joinToString(" ")}".capitalize()
            val originalName = "${surname}${originalChars.joinToString("")}"

            return "$fullEnglishName ($fullWadeGiles) - $originalName"

        } catch (e: Exception) {
            e.printStackTrace()
            return "转换失败"
        }
    }

    private fun convertToWadeGiles(pinyin: String): String {
        // 预处理：处理一些特殊情况
        var result = pinyin.toLowerCase()

        // 检查是否是特殊情况
        specialCases[result]?.let {
            return it.capitalize()
        }

        // 应用声母转换规则
        for ((key, value) in initialConsonants) {
            if (result.startsWith(key)) {
                result = value + result.substring(key.length)
                break
            }
        }

        // 应用韵母转换规则
        for ((key, value) in vowels) {
            val initialLength = result.takeWhile { !it.isVowel() }.length
            val restOfString = result.substring(initialLength)
            if (restOfString.startsWith(key)) {
                result = result.substring(0, initialLength) + value +
                        restOfString.substring(key.length)
                break
            }
        }

        // 特殊规则处理
        result = result
            .replace("v", "ü")
            .replace("([ptkch])'([^aeiouü]*)([aeiouü])".toRegex()) { matchResult ->
                // 处理声母后面的撇号位置
                "${matchResult.groupValues[1]}${matchResult.groupValues[2]}${matchResult.groupValues[3]}'"
            }

        // 处理词尾规则
        result = when {
            // 以 n 或 ng 结尾的不变
            result.endsWith("ng") || result.endsWith("n") -> result
            // 以 r 结尾转换为 rh
            result.endsWith("r") -> result + "h"
            // 以元音结尾加 h
            result.last().isVowel() -> result + "h"
            // 其他情况保持不变
            else -> result
        }

        // 处理声调位置（如果需要的话）
        result = result.replace("([aeiouü])'".toRegex()) { matchResult ->
            "${matchResult.groupValues[1]}'"
        }

        return result.capitalize()
    }
}