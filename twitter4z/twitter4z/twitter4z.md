!SLIDE

# Twitter4z

*[@halcat0x15a](http://twitter.com/#!/halcat0x15a)*

!SLIDE

# Twitter4zとは？

* Twitter APIのScala Wrapper
* *[Scalaz](http://code.google.com/p/scalaz/)*を用いた実装
* [github](https://github.com/halcat0x15a/twitter4z)で公開中

!SLIDE

# 使い方

## [README!](http://github.com/halcat0x15a/twitter4z/blob/master/README.md)

!SLIDE

# 実装の話

!SLIDE

## Source

* [Twitter](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/twitter4z/Twitter.scala.html)
	* [APIs](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/APIs.scala.html)
		* [Timelines](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/Timelines.scala.html)
		* [Tweets](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/Tweets.scala.html)

!SLIDE

## Twitter4zはresourceでできている！

!SLIDE

### twitter4z.api.resource

###### 中２病を拗らせたコード

```scala
def parse[A: JSONR](conn: HttpURLConnection): TwitterResult[A] = (
  TwitterValidation(
    fromJSON[A](Http.tryParse(conn.getInputStream, parseJson))
  ) <**> TwitterValidation(
    RateLimit(conn)
  )
)(TwitterResponse.apply)

def resource[A: JSONR /* 返る型 */](
  method: Method, // HTTPメソッド
  url: String, // Resource URL
  tokens: OptionTokens, // 認証用トークン
  parameters: Seq[Parameter]* // パラメータ
) = optOAuth(
  method(url).params(parameters.flatten.withFilter(null !=).map(_.value): _*)
)(tokens).processValidation(parse[A]).join
```

!SLIDE

## JSONR[A]

*[lift-json-scalaz](http://github.com/lift/framework/tree/master/core/json-scalaz)*というJSONライブラリに存在する型クラス。

インスタンスを作っておけば*fromJSON[A]*で対象*A*のインスタンスが得られます。

これは、オブジェクトと対にして作ります。

!SLIDE

## オブジェクト

* [RateLimitStatus](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/RateLimitStatus.scala.html)
* [AccountSettings](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/AccountSettings.scala.html)

!SLIDE

## パラメータ

Twitter APIでは多くの必須パラメータ、オプションパラメータがあります。

使用している[scalaj-http](http://github.com/scalaj/scalaj-http)では(String, String)の可変長引数を取り、パラメータが付加されたリクエストを作ります。

### [Parameter](http://halcat0x15a.github.com/twitter4z/core/target/scala-2.9.1/classes.sxr/XParameters.scala.html)

!SLIDE

## それにしてもTwitter REST APIは・・・

* パラメータ多すぎ
* オブジェクト多すぎ
* オブジェクトのフィールド多すぎ

###### ということで・・・・・

!SLIDE

# 自動生成の話

!SLIDE

## [SBT](http://github.com/harrah/xsbt/wiki)(simple-build-tool)

SBTにはソースコードを生成する機能があります。

sourceGeneratorsというもので、ファイルのシーケンスを渡すことでそのファイルを指定したタイミングで生成します。

* [Twitter4zBuild](http://github.com/halcat0x15a/twitter4z/blob/master/project/Twitter4zBuild.scala)
* [Generator.generate](http://github.com/halcat0x15a/twitter4z/blob/master/project/Generator.scala#L19)

!SLIDE

## Twitter REST APIに必要なもの

* パラメータとその型
* オブジェクト
	* フィールドとその型
* リソース
	* 関数名とその戻り値
	* HTTPメソッド
	* URL
	* 認証が必要かどうか
	* 必須パラメータとオプションパラメータ

これらを外部ファイルに記述してパースします。

!SLIDE

## resources

* [parameters](http://github.com/halcat0x15a/twitter4z/blob/master/core/src/main/resources/parameters)
* [account_settings](http://github.com/halcat0x15a/twitter4z/blob/master/core/src/main/resources/objects/account_settings)
* [timelines](http://github.com/halcat0x15a/twitter4z/blob/master/core/src/main/resources/api/timelines)

!SLIDE

## Parser

Scalaには標準でパーサライブラリがあり、その中の*[scala.util.parsing.combinator.RegexParser](http://www.scala-lang.org/api/current/index.html#scala.util.parsing.combinator.RegexParsers)*を使いました。

Parser Combinatorは小さなパーサを合成していき、より大きなものをパースします。

!SLIDE

## Generators

* [Generator](http://github.com/halcat0x15a/twitter4z/blob/master/project/Generator.scala)
	* [ParametersGenerator](http://github.com/halcat0x15a/twitter4z/blob/master/project/ParametersGenerator.scala)
	* [APIGenerator](http://github.com/halcat0x15a/twitter4z/blob/master/project/APIGenerator.scala)
	* [SpecsGenerator](http://github.com/halcat0x15a/twitter4z/blob/master/project/SpecsGenerator.scala)

!SLIDE

### みんなも自動化できるところは自動化しようぜ！

!SLIDE

# ありがとうございました
