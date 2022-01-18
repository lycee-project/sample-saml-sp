# sample-saml-sp

こちらのサイトにあるコードをgradleプロジェクトとして，書き直したものです．
また，認証機能にSAML認証の他にバックドアとしてフォーム認証を追加してあります．

https://codetinkering.com/saml2-spring-security-5-2-tutorial/

Identity Providerは [sample-saml-idp](https://github.com/lycee-project/sample-saml-idp) リポジトリを使ってます．


# 実装してある機能

## 画面一覧
| URL          | 画面名          | 概要                                                                      |
|:-------------|:-------------|:------------------------------------------------------------------------|
| /            | ホーム画面        | ログインしなくてもみれるページ<br>ログインしているとその情報が表示される                                  |
| /user/       | ログインすると見れる画面 | SAML認証後に表示されるページ<br>このページではログインした時のID（ユーザー名）が表示される                      |
| /secure/     | セキュアな画面      | SAML認証後にシステムが認可したユーザーのみが表示できるページ<br>認可判定は`app.login.admin.users`を利用している |
| /logout      | ログアウト        | ログアウト処理するページ<br>ログアウトごはホーム画面に遷移する                                       |
| /dummy/login | バックドア用ログイン画面 | SAML認証を利用せずにログインするためのページ<br>ログイン後はSAML認証後と同じ動きをとることが出来る                 |



## クラス

### SecurityConfig
Spring Securityの設定クラス  
SAML認証とフォーム認証の設定やURLの認証・認可設定が含まれている．  

### LoginUserDetails
認証後に生成されるオブジェクト  
このクラスに認証したユーザ名や認証情報を設定する．  
Controllerは，基本的にこのクラスを認証情報として処理する．
```java
@Controller
class SomeController {
  @RequestMapping("/")
  public String index(
    @AuthenticationPrincipal LoginUserDetails loginUserDetails  // 認証情報の取得
  ) {
    ...
  }
}
```

### LoginUserDetailsService
認証するためのデータを生成するクラス  
このクラスはSAML認証とフォーム認証の両方で利用され，統一された認証情報を生成することが出来るようにしてある．
本来はDBなどにアクセスして検証を行うが，このクラスはサンプルのため，パスワードが合っていればすべて認証されるようにしてある．  

