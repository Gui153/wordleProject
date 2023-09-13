package com.example.wordle

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.color
import com.example.wordle.databinding.ActivityMainBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

private var fourLetterWords = "Area,Army,Baby,Back,Ball,Band,Bank,Base,Bill,Body,Book,Call,Card,Care,Case,Cash,City,Club,Cost,Date,Deal,Door,Duty,East,Edge,Face,Fact,Farm,Fear,File,Film,Fire,Firm,Fish,Food,Foot,Form,Fund,Game,Girl,Goal,Gold,Hair,Half,Hall,Hand,Head,Help,Hill,Home,Hope,Hour,Idea,Jack,John,Kind,King,Lack,Lady,Land,Life,Line,List,Look,Lord,Loss,Love,Mark,Mary,Mind,Miss,Move,Name,Need,News,Note,Page,Pain,Pair,Park,Part,Past,Path,Paul,Plan,Play,Post,Race,Rain,Rate,Rest,Rise,Risk,Road,Rock,Role,Room,Rule,Sale,Seat,Shop,Show,Side,Sign,Site,Size,Skin,Sort,Star,Step,Task,Team,Term,Test,Text,Time,Tour,Town,Tree,Turn,Type,Unit,User,View,Wall,Week,West,Wife,Will,Wind,Wine,Wood,Word,Work,Year,Bear,Beat,Blow,Burn,Call,Care,Cast,Come,Cook,Cope,Cost,Dare,Deal,Deny,Draw,Drop,Earn,Face,Fail,Fall,Fear,Feel,Fill,Find,Form,Gain,Give,Grow,Hang,Hate,Have,Head,Hear,Help,Hide,Hold,Hope,Hurt,Join,Jump,Keep,Kill,Know,Land,Last,Lead,Lend,Lift,Like,Link,Live,Look,Lose,Love,Make,Mark,Meet,Mind,Miss,Move,Must,Name,Need,Note,Open,Pass,Pick,Plan,Play,Pray,Pull,Push,Read,Rely,Rest,Ride,Ring,Rise,Risk,Roll,Rule,Save,Seek,Seem,Sell,Send,Shed,Show,Shut,Sign,Sing,Slip,Sort,Stay,Step,Stop,Suit,Take,Talk,Tell,Tend,Test,Turn,Vary,View,Vote,Wait,Wake,Walk,Want,Warn,Wash,Wear,Will,Wish,Work,Able,Back,Bare,Bass,Blue,Bold,Busy,Calm,Cold,Cool,Damp,Dark,Dead,Deaf,Dear,Deep,Dual,Dull,Dumb,Easy,Evil,Fair,Fast,Fine,Firm,Flat,Fond,Foul,Free,Full,Glad,Good,Grey,Grim,Half,Hard,Head,High,Holy,Huge,Just,Keen,Kind,Last,Late,Lazy,Like,Live,Lone,Long,Loud,Main,Male,Mass,Mean,Mere,Mild,Nazi,Near,Neat,Next,Nice,Okay,Only,Open,Oral,Pale,Past,Pink,Poor,Pure,Rare,Real,Rear,Rich,Rude,Safe,Same,Sick,Slim,Slow,Soft,Sole,Sore,Sure,Tall,Then,Thin,Tidy,Tiny,Tory,Ugly,Vain,Vast,Very,Vice,Warm,Wary,Weak,Wide,Wild,Wise,Zero,Ably,Afar,Anew,Away,Back,Dead,Deep,Down,Duly,Easy,Else,Even,Ever,Fair,Fast,Flat,Full,Good,Half,Hard,Here,High,Home,Idly,Just,Late,Like,Live,Long,Loud,Much,Near,Nice,Okay,Once,Only,Over,Part,Past,Real,Slow,Solo,Soon,Sure,That,Then,This,Thus,Very,When,Wide"

object FourLetterWordList {
    // List of most common 4 letter words from: https://7esl.com/4-letter-words/

    // Returns a list of four letter words as a list
    fun getAllFourLetterWords(): List<String> {
        return fourLetterWords.split(",")
    }

    // Returns a random four letter word from the list in all caps
    fun getRandomFourLetterWord(): String {
        val allWords = getAllFourLetterWords()
        val randomNumber = (allWords.indices).shuffled().last()
        return allWords[randomNumber].uppercase()
    }
}


fun check(guess:String, actual:String): String {
    var buildOut = ""

    for(num in 0..3){
        if(guess[num] == actual[num]){
            buildOut += "O"
        }
        else if(guess[num] in actual){
            buildOut += "+"
        }
        else
            buildOut += "X"
    }
    println(buildOut)
    return buildOut
}

fun buildOut(num: Int, check: String, inp:String ): SpannableStringBuilder {

    val s = SpannableStringBuilder()
    s.append("GUESS #$num\t\t${inp}")
    s.append("\nGUESS #$num Check\t\t\t")
    for(i in 0..3){
        if(check[i] == 'O'){
            //append green inp[i]

            s.color(Color.GREEN){
                append(inp[i])
            }

        }
        else if(check[i] == '+'){
            // append orange inp[i]
            s.color(Color.parseColor("#FFA000")){
                append(inp[i])
            }

        }
        else {
            // append red
            s.color(Color.RED){
                append(inp[i])
            }

        }
    }
    s.append("\n")

    return s

}

public fun makeOptVise(opt1:Button, opt2:Button,opt3:Button, rule:TextView, but:Button, inp:EditText){
    opt1.visibility = View.VISIBLE
    opt2.visibility = View.VISIBLE
    opt3.visibility = View.VISIBLE
    rule.text = "Select one of the lists of animals"
    but.visibility = View.INVISIBLE
    inp.visibility = View.INVISIBLE
}



public fun makeOptInv(opt1:Button, opt2:Button,opt3:Button, rule:TextView, but:Button, inp:EditText, retrybut: Button){
    opt1.visibility = View.INVISIBLE
    opt2.visibility = View.INVISIBLE
    opt3.visibility = View.INVISIBLE
    rule.text = "Try to guess the 4 letter word in 3 tries"
    but.visibility = View.VISIBLE
    inp.visibility = View.VISIBLE
    retrybut.visibility = View.INVISIBLE
}


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val party = Party(
            speed= 0f,
            maxSpeed = 30f,
            damping=0.9f,
            spread=360,
            colors= listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter=Emitter(duration = 100, TimeUnit.MILLISECONDS ).max(100),
            position=Position.Relative(0.5,0.3)
        )
        val stre = findViewById<TextView>(R.id.streak)
        val outSave = SpannableStringBuilder()
        var word = FourLetterWordList.getRandomFourLetterWord()
        val inp = findViewById<EditText>(R.id.inputText)
        val but = findViewById<Button>(R.id.sub)
        val out = findViewById<TextView>(R.id.output)
        val retrybut = findViewById<Button>(R.id.retry)
        val err = findViewById<TextView>(R.id.error)
        var count = 1
        val conf = findViewById<KonfettiView>(R.id.confetti)
        val reg = Pattern.compile(/* regex = */ "^[a-zA-Z]{4}")
        var streakCount = 0

        println(word)

        val opt1 = findViewById<Button>(R.id.bird)
        val opt2 = findViewById<Button>(R.id.fish)
        val opt3 = findViewById<Button>(R.id.random)
        val rule = findViewById<TextView>(R.id.rule)


        makeOptVise(opt1,opt2,opt3,rule,but,inp)
        opt1.setOnClickListener(){
            count = 1
            fourLetterWords ="coot,chat,crow,coua,duck,dodo,dove,gull,guan,hawk,ibis,inca,iiwi,iora,knot,kite,kiwi,kagu,koel,lark,loon,myna,nene,rook,ruff,rail,rhea,ruby,skua,shag,swan,smew,sora,teal,tern,tody,wren,weka"
            makeOptInv(opt1,opt2,opt3,rule,but,inp,retrybut)
            word = FourLetterWordList.getRandomFourLetterWord()
            out.text = ""
            word = FourLetterWordList.getRandomFourLetterWord()
            println(word)
            inp.setText(/* text = */ "")
            but.visibility = View.VISIBLE
            outSave.clear()
            outSave.clearSpans()
            println(word)
        }
        opt2.setOnClickListener(){
            count = 1
            fourLetterWords = "barb,bass,boga,buri,chub,char,carp,cusk,dory,drum,dace,goby,hake,hoki,jack,ling,mola,mora,nase,opah,pike,rudd,rohu,scat,shad,sole,tuna,tang,tope,uaru,walu,scup,"
            makeOptInv(opt1,opt2,opt3,rule,but,inp,retrybut)
            word = FourLetterWordList.getRandomFourLetterWord()
            out.text = ""
            word = FourLetterWordList.getRandomFourLetterWord()
            println(word)
            inp.setText(/* text = */ "")
            but.visibility = View.VISIBLE
            outSave.clear()
            outSave.clearSpans()
            println(word)
        }
        opt3.setOnClickListener(){
            count = 1
            fourLetterWords ="barb,bass,boga,buri,chub,char,carp,cusk,dory,drum,dace,goby,hake,hoki,jack,ling,mola,mora,nase,opah,pike,rudd,rohu,scat,shad,sole,tuna,tang,tope,uaru,walu,scup," + "coot,chat,crow,coua,duck,dodo,dove,gull,guan,hawk,ibis,inca,iiwi,iora,knot,kite,kiwi,kagu,koel,lark,loon,myna,nene,rook,ruff,rail,rhea,ruby,skua,shag,swan,smew,sora,teal,tern,tody,wren,weka"
            makeOptInv(opt1,opt2,opt3,rule,but,inp,retrybut)
            word = FourLetterWordList.getRandomFourLetterWord()
            println(word)
            out.text = ""
            word = FourLetterWordList.getRandomFourLetterWord()
            println(word)
            inp.setText(/* text = */ "")
            but.visibility = View.VISIBLE
            outSave.clear()
            outSave.clearSpans()
        }




        binding.sub.setOnClickListener(){
            println(reg.matcher(inp.text).matches())
            if(count != 4) {
                if (inp.text.length < 4) {
                    err.text = "Error: word is too short"
                } else if (inp.text.length > 4) {
                    err.text = "Error: word is too long"
                } else if(!reg.matcher(inp.text).matches()){
                    err.text = "Error: no number or special characters allowed"
                }
                else {
                    err.text = ""

                    //out.text = out.text.toString() + "GUESS #$count\t\t${inp.text.toString().uppercase()}\nGUESS #$count Check\t\t" + check( inp.text.toString().uppercase(), word.uppercase()) + "\n"
                    outSave.append(buildOut(count,check( inp.text.toString().uppercase(), word.uppercase()),inp.text.toString().uppercase()))
                    out.setText( outSave, TextView.BufferType.SPANNABLE)
                    count++
                    println(word)
                    if(check(inp.text.toString().uppercase(), word.uppercase()).compareTo("OOOO") == 0){
                        makeOptVise(opt1,opt2,opt3,rule,but,inp)
                        binding.confetti.start(party)
                        outSave.append("Congratulations you won")
                        streakCount++
                        out.setText(outSave, TextView.BufferType.SPANNABLE)
                        count = 4
                        stre.text = ""+streakCount
                        retrybut.visibility = View.VISIBLE
                        but.visibility = View.INVISIBLE
                    }
                    else if(count == 4){
                        makeOptVise(opt1,opt2,opt3,rule,but,inp)
                        outSave.append("Sorry you lost\nThe correct word was: $word")
                        out.setText(outSave, TextView.BufferType.SPANNABLE)
                        retrybut.visibility = View.VISIBLE
                        but.visibility = View.INVISIBLE
                    }
                    }

                }
            }


        retrybut.setOnClickListener(){
            count = 1
            makeOptInv(opt1,opt2,opt3,rule,but,inp,retrybut)
            out.text = ""
            word = FourLetterWordList.getRandomFourLetterWord()
            println(word)
            inp.setText(/* text = */ "")
            but.visibility = View.VISIBLE
            outSave.clear()
            outSave.clearSpans()
        }

    }
}