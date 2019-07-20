require "json"

package = JSON.parse(File.read(File.join(File.dirname(__FILE__), "package.json")))

Pod::Spec.new do |s|
  s.name             = 'TXIm'
  s.version          = package['version']
  s.summary          = package['description']
  s.description      = package['description']
  s.homepage         = package['homepage']
  s.license          = package['license']
  s.author           = package['author']
  s.source           = { :git => package['repository']['url'], :tag => s.version.to_s }

  s.ios.deployment_target = '9.0'
  s.prefix_header_file    = 'ios/RCTTxim/txim.pch'
  s.source_files          = 'ios/RCTTxim/{Constant,Listener,Message,Module}/*.{h,m}', 'ios/RCTTxim/*.{h,m}'
  s.vendored_frameworks   = 'ios/RCTTxim/*.framework'

  s.dependency 'React'
end
